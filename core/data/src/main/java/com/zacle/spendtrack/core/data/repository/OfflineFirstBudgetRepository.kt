package com.zacle.spendtrack.core.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.RemoteBudgetData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedBudgetDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableBudgetDataSource
import com.zacle.spendtrack.core.data.notification.BudgetAlertNotifier
import com.zacle.spendtrack.core.data.sync.RecurrentBudgetWorker
import com.zacle.spendtrack.core.data.sync.SyncBudgetWorker
import com.zacle.spendtrack.core.data.sync.SyncConstraints
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.DeletedBudget
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.util.Synchronizer
import com.zacle.spendtrack.core.model.util.changeLastSyncTimes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Offline-first implementation of [BudgetRepository] that uses both a network and an offline storage
 * source. When a budget is first requested, it is requested from the local database, and if empty,
 * we check if the user is online. If they are, we request the budget from the network and save it
 * in the local database.
 *
 * Add, Update and Delete Budget first in the local database. Then if the user is online, we do the
 * same in the network. If the user is offline, we schedule a background work using [WorkManager]
 */
class OfflineFirstBudgetRepository @Inject constructor(
    @LocalBudgetData private val localBudgetDataSource: SyncableBudgetDataSource,
    @RemoteBudgetData private val remoteBudgetDataSource: BudgetDataSource,
    private val deletedBudgetDataSource: DeletedBudgetDataSource,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val budgetAlarmNotifier: BudgetAlertNotifier,
): BudgetRepository {
    override suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>> =
        localBudgetDataSource.getBudgets(userId, budgetPeriod).flatMapLatest { budgets ->
            val isOnline = networkMonitor.isOnline.first()
            if (budgets.isEmpty() && isOnline) {
                remoteBudgetDataSource.getBudgets(userId, budgetPeriod).flatMapLatest { remoteBudgets ->
                    localBudgetDataSource.addAllBudgets(remoteBudgets)
                    // Schedule a recurrent budget work for each budget obtained from the network
                    remoteBudgets.forEach { budget ->
                        if (budget.recurrent) {
                            RecurrentBudgetWorker.scheduleNextRecurrentBudgetWork(budget, context)
                        }
                    }
                    flow { emit(remoteBudgets) }
                }
            } else {
                flow { emit(budgets) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getBudget(
        userId: String,
        budgetId: String
    ): Flow<Budget?> = localBudgetDataSource.getBudget(userId, budgetId)
        .flatMapLatest { budget ->
            val isOnline = networkMonitor.isOnline.first()
            if (budget == null && isOnline) {
                remoteBudgetDataSource.getBudget(userId, budgetId).flatMapLatest { remoteBudget ->
                    if (remoteBudget != null) {
                        localBudgetDataSource.addBudget(remoteBudget)
                    }
                    flow { emit(remoteBudget) }
                }
            } else {
                flow { emit(budget) }
            }
        }

    override suspend fun addBudget(budget: Budget) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.addBudget(budget)
            localBudgetDataSource.addBudget(budget.copy(synced = true))
        } else {
            startUpSyncWork(budget.userId)
            localBudgetDataSource.addBudget(budget)
        }

        // If the budget is recurrent, schedule the next recurrent budget work
        if (budget.recurrent) {
            RecurrentBudgetWorker.scheduleNextRecurrentBudgetWork(budget, context)
        }
    }

    override suspend fun updateBudget(budget: Budget) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.updateBudget(budget)
            localBudgetDataSource.updateBudget(budget.copy(synced = true))
        } else {
            startUpSyncWork(budget.userId)
            localBudgetDataSource.updateBudget(budget.copy(synced = false))
        }

        // Send a notification to the user if the budget is exceeded or about to be exceeded
        if (budget.budgetAlert) {
            if (budget.remainingAmount <= 0) {
                budgetAlarmNotifier.showBudgetAlertNotification(true)
            } else {
                val amountSpentPercentage = (budget.amount - budget.remainingAmount) / budget.amount
                if (amountSpentPercentage >= budget.budgetAlertPercentage) {
                    budgetAlarmNotifier.showBudgetAlertNotification(false)
                }
            }
        }

        // If the budget is recurrent, cancel the next recurrent budget work and reschedule the next recurrent budget work
        if (budget.recurrent) {
            WorkManager
                .getInstance(context)
                .cancelUniqueWork("RecurrentBudgetWork_${budget.category.categoryId}")
            RecurrentBudgetWorker.scheduleNextRecurrentBudgetWork(budget, context)
        }
    }

    override suspend fun deleteBudget(budget: Budget) {
        localBudgetDataSource.deleteBudget(budget.userId, budget.budgetId)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.deleteBudget(budget.userId, budget.budgetId)
        } else {
            startUpSyncWork(budget.userId)
            deletedBudgetDataSource.insert(DeletedBudget(budget.budgetId, budget.userId))
        }

        // If the budget is recurrent, cancel the next recurrent budget work
        if (budget.recurrent) {
            WorkManager
                .getInstance(context)
                .cancelUniqueWork("RecurrentBudgetWork_${budget.category.categoryId}")
        }
    }

    private fun startUpSyncWork(userId: String) {
        val inputData = Data.Builder()
            .putString(USER_ID_KEY, userId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncBudgetWorker>()
            .setConstraints(SyncConstraints)
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val operation = WorkManager.getInstance(context)
            .enqueueUniqueWork(
                BUDGET_SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                workRequest
            )
            .result

        operation.addListener(
            { Timber.i("SyncBudgetWorker enqueued") },
            { it.run() }
        )
    }

    /**
     * Syncs the local and remote data sources
     */
    override suspend fun syncWith(userId: String, synchronizer: Synchronizer): Boolean =
        synchronizer.changeLastSyncTimes(
            lastSyncUpdater = { copy(budgetLastSync = it) },
            modelAdder = {
                val addedBudgetsNotSynced = localBudgetDataSource.getNonSyncedBudgets(userId)

                addedBudgetsNotSynced.forEach { budget ->
                    withContext(NonCancellable) {
                        try {
                            remoteBudgetDataSource.addBudget(budget)
                            localBudgetDataSource.updateBudget(budget.copy(synced = true))
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            },
            modelUpdater = {
                val updatedBudgetsNotSynced = localBudgetDataSource.getNonSyncedBudgets(userId)

                updatedBudgetsNotSynced.forEach { budget ->
                    withContext(NonCancellable) {
                        try {
                            remoteBudgetDataSource.updateBudget(budget)
                            localBudgetDataSource.updateBudget(budget.copy(synced = true))
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            },
            modelDeleter = {
                val deletedBudgetIds = deletedBudgetDataSource.getDeletedBudgets(userId).map { it.budgetId }

                deletedBudgetIds.forEach { budgetId ->
                    withContext(NonCancellable) {
                        try {
                            remoteBudgetDataSource.deleteBudget(userId, budgetId)
                            deletedBudgetDataSource.delete(userId, budgetId)
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            }
        )
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val BUDGET_SYNC_WORK_NAME = "BudgetSyncWorkName"