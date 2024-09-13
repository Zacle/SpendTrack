package com.zacle.spendtrack.core.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.LocalExpenseData
import com.zacle.spendtrack.core.common.di.RemoteExpenseData
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.data.datasource.DeletedExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.ExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.SyncableExpenseDataSource
import com.zacle.spendtrack.core.data.sync.SyncConstraints
import com.zacle.spendtrack.core.data.sync.SyncExpenseWorker
import com.zacle.spendtrack.core.domain.repository.ExpenseRepository
import com.zacle.spendtrack.core.model.DeletedExpense
import com.zacle.spendtrack.core.model.Expense
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
 * Offline-first implementation of [ExpenseRepository] that uses both a network and an offline storage
 * source. When a expense is first requested, it is requested from the local database, and if empty,
 * we check if the user is online. If they are, we request the expense from the network and save it
 * in the local database.
 *
 * Add, Update and Delete Expense first in the local database. Then if the user is online, we do the
 * same in the network. If the user is offline, we schedule a background work using [WorkManager]
 */
class OfflineFirstExpenseRepository @Inject constructor(
    @LocalExpenseData private val localExpenseDataSource: SyncableExpenseDataSource,
    @RemoteExpenseData private val remoteExpenseDataSource: ExpenseDataSource,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val deletedExpenseDataSource: DeletedExpenseDataSource
): ExpenseRepository {
    override suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>> =
        localExpenseDataSource.getExpenses(userId, period).flatMapLatest { expenses ->
            val isOnline = networkMonitor.isOnline.first()
            if (expenses.isEmpty() && isOnline) {
                remoteExpenseDataSource.getExpenses(userId, period).flatMapLatest { remoteExpenses ->
                    remoteExpenses.forEach { expense ->
                        localExpenseDataSource.addExpense(expense)
                    }
                    flow { emit(remoteExpenses) }
                }
            } else {
                flow { emit(expenses) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getExpensesByCategory(
        userId: String,
        categoryId: String,
        period: Period
    ): Flow<List<Expense>> = localExpenseDataSource.getExpensesByCategory(userId, categoryId, period)
        .flatMapLatest { expenses ->
            val isOnline = networkMonitor.isOnline.first()
            if (expenses.isEmpty() && isOnline) {
                remoteExpenseDataSource.getExpensesByCategory(userId, categoryId, period).flatMapLatest { remoteExpenses ->
                    remoteExpenses.forEach { expense ->
                        localExpenseDataSource.addExpense(expense)
                    }
                    flow { emit(remoteExpenses) }
                }
            } else {
                flow { emit(expenses) }
            }
        }.flowOn(ioDispatcher)

    override suspend fun getExpense(userId: String, expenseId: String): Flow<Expense?> =
        localExpenseDataSource.getExpense(userId, expenseId).flatMapLatest { expense ->
            val isOnline = networkMonitor.isOnline.first()
            if (expense == null && isOnline) {
                remoteExpenseDataSource.getExpense(userId, expenseId).flatMapLatest { remoteExpense ->
                    if (remoteExpense != null) {
                        localExpenseDataSource.addExpense(remoteExpense)
                    }
                    flow { emit(remoteExpense) }
                }
            } else {
                flow { emit(expense) }
            }
        }

    override suspend fun addExpense(expense: Expense) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteExpenseDataSource.addExpense(expense)
            localExpenseDataSource.addExpense(expense.copy(synced = true))
        } else {
            startUpSyncWork(expense.userId)
            localExpenseDataSource.addExpense(expense)
        }
    }

    override suspend fun updateExpense(expense: Expense) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteExpenseDataSource.updateExpense(expense)
            localExpenseDataSource.updateExpense(expense.copy(synced = true))
        } else {
            startUpSyncWork(expense.userId)
            localExpenseDataSource.updateExpense(expense.copy(synced = false))
        }
    }

    override suspend fun deleteExpense(expense: Expense) {
        localExpenseDataSource.deleteExpense(expense.userId, expense.id)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            remoteExpenseDataSource.deleteExpense(expense.userId, expense.id)
        } else {
            startUpSyncWork(expense.userId)
            deletedExpenseDataSource.insert(DeletedExpense(expense.id, expense.userId))
        }
    }

    private fun startUpSyncWork(userId: String) {
        val inputData = Data.Builder()
            .putString(USER_ID_KEY, userId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncExpenseWorker>()
            .setConstraints(SyncConstraints)
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val operation = WorkManager.getInstance(context)
            .enqueueUniqueWork(
                EXPENSE_SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                workRequest
            )
            .result

        operation.addListener(
            { Timber.i("SyncExpenseWorker enqueued") },
            { it.run() }
        )
    }

    override suspend fun syncWith(userId: String, synchronizer: Synchronizer): Boolean =
        synchronizer.changeLastSyncTimes(
            lastSyncUpdater = { copy(expenseLastSync = it) },
            modelAdder = {
                val addedExpensesNotSynced = localExpenseDataSource.getNonSyncedExpenses(userId)

                addedExpensesNotSynced.forEach { expense ->
                    try {
                        remoteExpenseDataSource.addExpense(expense)
                        localExpenseDataSource.updateExpense(expense.copy(synced = true))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelUpdater = {
                val updatedExpensesNotSynced = localExpenseDataSource.getNonSyncedExpenses(userId)

                updatedExpensesNotSynced.forEach { expense ->
                    try {
                        remoteExpenseDataSource.updateExpense(expense)
                        localExpenseDataSource.updateExpense(expense.copy(synced = true))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelDeleter = {
                val deletedExpenseIds = deletedExpenseDataSource.getDeletedExpenses(userId).map { it.expenseId }

                deletedExpenseIds.forEach { expenseId ->
                    try {
                        remoteExpenseDataSource.deleteExpense(userId, expenseId)
                        deletedExpenseDataSource.delete(userId, expenseId)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        )
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val EXPENSE_SYNC_WORK_NAME = "ExpenseSyncWorkName"