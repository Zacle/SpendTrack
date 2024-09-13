package com.zacle.spendtrack.core.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalIncomeData
import com.zacle.spendtrack.core.common.di.RemoteIncomeData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.data.datasource.DeletedIncomeDataSource
import com.zacle.spendtrack.core.data.datasource.IncomeDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableIncomeDataSource
import com.zacle.spendtrack.core.data.sync.SyncConstraints
import com.zacle.spendtrack.core.data.sync.SyncIncomeWorker
import com.zacle.spendtrack.core.domain.repository.IncomeRepository
import com.zacle.spendtrack.core.model.DeletedIncome
import com.zacle.spendtrack.core.model.Income
import com.zacle.spendtrack.core.model.Period
import com.zacle.spendtrack.core.model.util.Synchronizer
import com.zacle.spendtrack.core.model.util.changeLastSyncTimes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

/**
 * Offline-first implementation of [IncomeRepository] that uses both a network and an offline storage
 * source. When a income is first requested, it is requested from the local database, and if empty,
 * we check if the user is online. If they are, we request the income from the network and save it
 * in the local database.
 *
 * Add, Update and Delete Income first in the local database. Then if the user is online, we do the
 * same in the network. If the user is offline, we schedule a background work using [WorkManager]
 */
class OfflineFirstIncomeRepository @Inject constructor(
    @LocalIncomeData private val localIncomeDataSource: SyncableIncomeDataSource,
    @RemoteIncomeData private val remoteIncomeDataSource: IncomeDataSource,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val deletedIncomeDataSource: DeletedIncomeDataSource
): IncomeRepository {
    override suspend fun getIncomes(userId: String, period: Period): Flow<List<Income>> =
        localIncomeDataSource.getIncomes(userId, period).flatMapLatest { incomes ->
            val isOnline = networkMonitor.isOnline.first()
            if (incomes.isEmpty() && isOnline) {
                remoteIncomeDataSource.getIncomes(userId, period).flatMapLatest { remoteIncomes ->
                    remoteIncomes.forEach { income ->
                        localIncomeDataSource.addIncome(income)
                    }
                    flow { emit(remoteIncomes) }
                }
            } else {
                flow { emit(incomes) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getIncomesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Income>> = localIncomeDataSource.getIncomesByCategory(userId, categoryId, period)
        .flatMapLatest { incomes ->
            val isOnline = networkMonitor.isOnline.first()
            if (incomes.isEmpty() && isOnline) {
                remoteIncomeDataSource.getIncomesByCategory(userId, categoryId, period).flatMapLatest { remoteIncomes ->
                    remoteIncomes.forEach { income ->
                        localIncomeDataSource.addIncome(income)
                    }
                    flow { emit(remoteIncomes) }
                }
            } else {
                flow { emit(incomes) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getIncome(userId: String, incomeId: String): Flow<Income?> =
        localIncomeDataSource.getIncome(userId, incomeId).flatMapLatest { income ->
            val isOnline = networkMonitor.isOnline.first()
            if (income == null && isOnline) {
                remoteIncomeDataSource.getIncome(userId, incomeId).flatMapLatest { remoteIncome ->
                    if (remoteIncome != null) {
                        localIncomeDataSource.addIncome(remoteIncome)
                    }
                    flow { emit(remoteIncome) }
                }
            } else {
                flow { emit(income) }
            }
        }

    override suspend fun addIncome(income: Income) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteIncomeDataSource.addIncome(income)
            localIncomeDataSource.addIncome(income.copy(synced = true))
        } else {
            startUpSyncWork(income.userId)
            localIncomeDataSource.addIncome(income)
        }
    }

    override suspend fun updateIncome(income: Income) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteIncomeDataSource.updateIncome(income)
            localIncomeDataSource.updateIncome(income.copy(synced = true))
        } else {
            startUpSyncWork(income.userId)
            localIncomeDataSource.updateIncome(income.copy(synced = false))
        }
    }

    override suspend fun deleteIncome(income: Income) {
        localIncomeDataSource.deleteIncome(income.userId, income.id)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteIncomeDataSource.deleteIncome(income.userId, income.id)
        } else {
            startUpSyncWork(income.userId)
            deletedIncomeDataSource.insert(DeletedIncome(income.id, income.userId))
        }
    }

    private fun startUpSyncWork(userId: String) {
        val inputData = Data.Builder()
            .putString(USER_ID_KEY, userId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncIncomeWorker>()
            .setConstraints(SyncConstraints)
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val operation = WorkManager.getInstance(context)
            .enqueueUniqueWork(
                INCOME_SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                workRequest
            )
            .result

        operation.addListener(
            { Timber.i("SyncIncomeWorker enqueued") },
            { it.run() }
        )
    }

    override suspend fun syncWith(userId: String, synchronizer: Synchronizer): Boolean =
        synchronizer.changeLastSyncTimes(
            lastSyncUpdater = { copy(incomeLastSync = it) },
            modelAdder = {
                val addedIncomesNotSynced = localIncomeDataSource.getNonSyncedIncomes(userId)

                addedIncomesNotSynced.forEach { income ->
                    try {
                        remoteIncomeDataSource.addIncome(income)
                        localIncomeDataSource.updateIncome(income.copy(synced = true))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelUpdater = {
                val updatedIncomesNotSynced = localIncomeDataSource.getNonSyncedIncomes(userId)

                updatedIncomesNotSynced.forEach { income ->
                    try {
                        remoteIncomeDataSource.updateIncome(income)
                        localIncomeDataSource.updateIncome(income.copy(synced = true))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelDeleter = {
                val deletedIncomeIds = deletedIncomeDataSource.getDeletedIncomes(userId).map { it.incomeId }

                deletedIncomeIds.forEach { incomeId ->
                    try {
                        remoteIncomeDataSource.deleteIncome(userId, incomeId)
                        deletedIncomeDataSource.delete(userId, incomeId)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        )
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val INCOME_SYNC_WORK_NAME = "IncomeSyncWorkName"