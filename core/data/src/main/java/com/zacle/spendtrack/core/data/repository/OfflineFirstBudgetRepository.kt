package com.zacle.spendtrack.core.data.repository

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalBudgetData
import com.zacle.spendtrack.core.common.di.RemoteBudgetData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.datasource.BudgetDataSource
import com.zacle.spendtrack.core.data.datasource.DeletedBudgetDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableBudgetDataSource
import com.zacle.spendtrack.core.domain.repository.BudgetRepository
import com.zacle.spendtrack.core.model.Budget
import com.zacle.spendtrack.core.model.DeletedBudget
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.util.Synchronizer
import com.zacle.spendtrack.core.model.util.changeLastSyncTimes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkMonitor: NetworkMonitor,
    private val deletedBudgetDataSource: DeletedBudgetDataSource
): BudgetRepository {
    override suspend fun getBudgets(userId: String, budgetPeriod: Period): Flow<List<Budget>> =
        localBudgetDataSource.getBudgets(userId, budgetPeriod).flatMapLatest { budgets ->
            val isOnline = networkMonitor.isOnline.first()
            if (budgets.isEmpty() && isOnline) {
                val remoteBudgets = remoteBudgetDataSource.getBudgets(userId, budgetPeriod).first()
                remoteBudgets.forEach { budget ->
                    localBudgetDataSource.addBudget(budget)
                }
                flow { emit(remoteBudgets) }
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
                val remoteBudget = remoteBudgetDataSource.getBudget(userId, budgetId).first()
                if (remoteBudget != null) {
                    localBudgetDataSource.addBudget(remoteBudget)
                }
                flow { emit(remoteBudget) }
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
            // TODO: Handle offline case
            localBudgetDataSource.addBudget(budget)
        }
    }

    override suspend fun updateBudget(budget: Budget) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.updateBudget(budget)
            localBudgetDataSource.updateBudget(budget.copy(synced = true))
        } else {
            // TODO: Handle offline case
            localBudgetDataSource.updateBudget(budget.copy(synced = false))
        }
    }

    override suspend fun deleteBudget(budget: Budget) {
        localBudgetDataSource.deleteBudget(budget.userId, budget.budgetId)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteBudgetDataSource.deleteBudget(budget.userId, budget.budgetId)
        } else {
            // TODO: Handle offline case
            deletedBudgetDataSource.insert(DeletedBudget(budget.budgetId, budget.userId))
        }
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
                    try {
                        remoteBudgetDataSource.addBudget(budget)
                        localBudgetDataSource.updateBudget(budget.copy(synced = true))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelUpdater = {
                val updatedBudgetsNotSynced = localBudgetDataSource.getNonSyncedBudgets(userId)

                updatedBudgetsNotSynced.forEach { budget ->
                    try {
                        remoteBudgetDataSource.updateBudget(budget)
                        localBudgetDataSource.updateBudget(budget.copy(synced = true))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelDeleter = {
                val deletedBudgetIds = deletedBudgetDataSource.getDeletedBudgets(userId).map { it.budgetId }

                deletedBudgetIds.forEach { budgetId ->
                    try {
                        remoteBudgetDataSource.deleteBudget(userId, budgetId)
                        deletedBudgetDataSource.delete(userId, budgetId)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        )
}