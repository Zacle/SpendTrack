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
import com.zacle.spendtrack.core.common.util.ImageStorageManager
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.data.datasource.DeletedExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.ExpenseDataSource
import com.zacle.spendtrack.core.data.datasource.StorageDataSource
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
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
    private val deletedExpenseDataSource: DeletedExpenseDataSource,
    private val networkMonitor: NetworkMonitor,
    private val storageDataSource: StorageDataSource,
    private val imageStorageManager: ImageStorageManager
): ExpenseRepository {
    override suspend fun getExpenses(userId: String, period: Period): Flow<List<Expense>> =
        localExpenseDataSource.getExpenses(userId, period).flatMapLatest { expenses ->
            val isOnline = networkMonitor.isOnline.first()
            if (expenses.isEmpty() && isOnline) {
                val remoteExpenses = remoteExpenseDataSource.getExpenses(userId, period).first()
                Timber.d("Syncing expenses from server = ${remoteExpenses.count()}")
                localExpenseDataSource.addAllExpenses(remoteExpenses)
                flowOf(remoteExpenses)
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
                    localExpenseDataSource.addAllExpenses(remoteExpenses)
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

    /**
     * Adds an expense to the local and remote data sources.
     * Handles both online and offline scenarios and ensures that the expense and its receipt image are synchronized.
     *
     * Steps:
     * 1. If online:
     *    - If a local receipt image exists, upload it to cloud storage.
     *    - Update the expense with the cloud receipt URL and clear the local image path.
     *    - Add the expense to the remote data source.
     *    - Mark the expense as synced and store it locally.
     *    - If any step fails (e.g., network issues), save the expense locally as unsynced for later synchronization.
     * 2. If offline:
     *    - Add the expense to the local database.
     *    - Mark the expense as unsynced.
     *    - Schedule WorkManager to sync the expense when the network is available.
     *
     * @param expense The expense object containing transaction data, including receipt information.
     */
    override suspend fun addExpense(expense: Expense) {
        // Check the current network status (online or offline)
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            addExpenseToServer(expense)
        } else {
            localExpenseDataSource.addExpense(expense)
            // Schedule WorkManager to sync the expense when back online
            startUpSyncWork(expense.userId)
        }
    }

    private suspend fun addExpenseToServer(expense: Expense) {
        var expenseToUpload = expense
        // If the expense has a local receipt image, upload it to the cloud
        if (expense.localReceiptImagePath != null) {
            // Upload the image to the cloud storage and obtain the cloud URL
            val cloudUrl = storageDataSource.uploadImageToCloud(
                bucketName = "$EXPENSE_IMAGES/${expense.id}.jpg",
                imagePath = expense.localReceiptImagePath!!
            )
            if (cloudUrl != null) {
                // Update the expense with the cloud receipt URL and clear the local receipt image path
                expenseToUpload = expense.copy(receiptUrl = cloudUrl, localReceiptImagePath = null)
            }
        }

        // Upload the expense to the remote data source (Firebase, etc.)
        remoteExpenseDataSource.addExpense(expenseToUpload)

        // Mark the expense as synced (synced = true) and save it locally
        localExpenseDataSource.addExpense(expenseToUpload.copy(synced = true))
    }

    /**
     * Updates an existing expense in both local and remote data sources.
     * This function handles both online and offline cases, ensuring the expense is synchronized when the user is online.
     *
     * Steps:
     * 1. If online:
     *    - If a local receipt image exists, upload it to cloud storage.
     *    - Update the expense with the cloud receipt URL and clear the local image path.
     *    - Update the expense in the remote data source.
     *    - Mark the expense as synced and update it in the local database.
     * 2. If offline:
     *    - Update the expense in the local database and mark it as unsynced.
     *    - Schedule WorkManager to sync the updated expense when the network is available.
     *
     * @param expense The expense object that contains updated transaction details, including receipt information.
     */
    override suspend fun updateExpense(expense: Expense) {
        // Check the current network status (online or offline)
        val isOnline = networkMonitor.isOnline.first()

        if (isOnline) {
            updateExpenseOnServer(expense)
        } else {
            localExpenseDataSource.updateExpense(expense.copy(synced = false))
            // Schedule WorkManager to sync the updated expense when the device goes online
            startUpSyncWork(expense.userId)
        }
    }

    private suspend fun updateExpenseOnServer(expense: Expense) {
        var expenseToUpload = expense
        val localReceiptImagePath = expense.localReceiptImagePath

        // If the expense has a local receipt image, upload it to cloud storage
        if (localReceiptImagePath != null) {
            // Upload the image to the cloud storage and get the cloud URL
            val cloudUrl = storageDataSource.uploadImageToCloud(
                bucketName = "$EXPENSE_IMAGES/${expense.id}.jpg",
                imagePath = localReceiptImagePath
            )
            // If the upload is successful, update the expense with the cloud receipt URL
            if (cloudUrl != null) {
                expenseToUpload = expense.copy(receiptUrl = cloudUrl, localReceiptImagePath = null)
                // Delete the local image file after uploading to avoid storage quota errors
                imageStorageManager.deleteImageLocally(localReceiptImagePath)
            }
        }
        // Update the expense in the remote data source (e.g., Firebase)
        remoteExpenseDataSource.updateExpense(expenseToUpload)

        // Mark the expense as synced and update it in the local database
        localExpenseDataSource.updateExpense(expenseToUpload.copy(synced = true))
    }

    /**
     * Deletes an expense from both local and remote data sources, handling both online and offline scenarios.
     *
     * Steps:
     * 1. Always delete the expense from the local data source.
     *    - If a local receipt image path exists, delete the local image file.
     * 2. If the device is online:
     *    - Delete the receipt image from cloud storage if a receipt URL exists.
     *    - Delete the expense from the remote data source (e.g., Firebase).
     * 3. If the device is offline:
     *    - Schedule WorkManager to sync the deleted expense with the remote server when the device is online.
     *    - Log the deletion in a local table (e.g., `DeletedExpense`) to track items for future deletion in the remote database.
     *
     * @param expense The expense object to be deleted, which contains the receipt information and ID.
     */
    override suspend fun deleteExpense(expense: Expense) {
        val localReceiptImagePath = expense.localReceiptImagePath

        // Delete the expense from the local database
        localExpenseDataSource.deleteExpense(expense.userId, expense.id)

        // If there is a local receipt image, delete it from local storage
        if (localReceiptImagePath != null) {
            imageStorageManager.deleteImageLocally(localReceiptImagePath)
        }

        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            deleteExpenseOnServer(expense)
        } else {
            deletedExpenseDataSource.insert(DeletedExpense(expense.id, expense.userId))
            // If offline, schedule a WorkManager task to sync deletions when back online
            startUpSyncWork(expense.userId)
        }
    }

    private suspend fun deleteExpenseOnServer(expense: Expense) {
        // Retrieve the receipt URL and local image path from the expense
        val receiptUrl = expense.receiptUrl
        // If online, delete the receipt from cloud storage (if it exists)
        if (receiptUrl != null) {
            storageDataSource.deleteImageFromCloud(
                bucketName = "$EXPENSE_IMAGES/${expense.id}.jpg",
                imagePath = receiptUrl
            )
        }
        // Delete the expense from the remote database (e.g., Firebase)
        remoteExpenseDataSource.deleteExpense(expense.userId, expense.id)
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
                    withContext(NonCancellable) {
                        try {
                            addExpenseToServer(expense)
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            },
            modelUpdater = {
                val updatedExpensesNotSynced = localExpenseDataSource.getNonSyncedExpenses(userId)

                updatedExpensesNotSynced.forEach { expense ->
                    withContext(NonCancellable) {
                        try {
                            updateExpenseOnServer(expense)
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            },
            modelDeleter = {
                val deletedExpenseIds = deletedExpenseDataSource.getDeletedExpenses(userId).map { it.expenseId }

                deletedExpenseIds.forEach { expenseId ->
                    withContext(NonCancellable) {
                        try {
                            remoteExpenseDataSource.deleteExpense(userId, expenseId)
                            deletedExpenseDataSource.delete(userId, expenseId)
                        } catch (e: Exception) {
                            Timber.e(e)
                        }
                    }
                }
            }
        )
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val EXPENSE_SYNC_WORK_NAME = "ExpenseSyncWorkName"

internal const val EXPENSE_IMAGES = "expenses"