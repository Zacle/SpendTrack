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
import com.zacle.spendtrack.core.common.util.ImageStorageManager
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.data.datasource.DeletedIncomeDataSource
import com.zacle.spendtrack.core.data.datasource.IncomeDataSource
import com.zacle.spendtrack.core.data.datasource.StorageDataSource
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
import kotlinx.coroutines.flow.flowOf
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
    private val deletedIncomeDataSource: DeletedIncomeDataSource,
    private val networkMonitor: NetworkMonitor,
    private val storageDataSource: StorageDataSource,
    private val imageStorageManager: ImageStorageManager
): IncomeRepository {
    override suspend fun getIncomes(userId: String, period: Period): Flow<List<Income>> =
        localIncomeDataSource.getIncomes(userId, period).flatMapLatest { incomes ->
            val isOnline = networkMonitor.isOnline.first()
            if (incomes.isEmpty() && isOnline) {
                val remoteIncomes = remoteIncomeDataSource.getIncomes(userId, period).first()
                Timber.d("Syncing incomes from server = ${remoteIncomes.count()}")
                localIncomeDataSource.addAllIncomes(remoteIncomes)
                flowOf(remoteIncomes)
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
                    localIncomeDataSource.addAllIncomes(remoteIncomes)
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

    /**
     * Adds an income to the local and remote data sources.
     * Handles both online and offline scenarios and ensures that the income and its receipt image are synchronized.
     *
     * Steps:
     * 1. If online:
     *    - If a local receipt image exists, upload it to cloud storage.
     *    - Update the income with the cloud receipt URL and clear the local image path.
     *    - Add the income to the remote data source.
     *    - Mark the income as synced and store it locally.
     *    - If any step fails (e.g., network issues), save the income locally as unsynced for later synchronization.
     * 2. If offline:
     *    - Add the income to the local database.
     *    - Mark the income as unsynced.
     *    - Schedule WorkManager to sync the income when the network is available.
     *
     * @param income The income object containing transaction data, including receipt information.
     */
    override suspend fun addIncome(income: Income) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            addIncomeToServer(income)
        } else {
            startUpSyncWork(income.userId)
            localIncomeDataSource.addIncome(income)
        }
    }

    private suspend fun addIncomeToServer(income: Income) {
        var incomeToUpload = income
        // If the income has a local receipt image, upload it to the cloud
        if (income.localReceiptImagePath != null) {
            // Upload the image to the cloud storage and obtain the cloud URL
            val cloudUrl = storageDataSource.uploadImageToCloud(
                bucketName = "$INCOME_IMAGES/${income.id}.jpg",
                imagePath = income.localReceiptImagePath!!
            )
            if (cloudUrl != null) {
                // Update the income with the cloud receipt URL and clear the local receipt image path
                incomeToUpload = income.copy(receiptUrl = cloudUrl, localReceiptImagePath = null)
            }
        }
        // Upload the income to the remote data source (Firebase, etc.)
        remoteIncomeDataSource.addIncome(incomeToUpload)

        // Mark the income as synced (synced = true) and save it locally
        localIncomeDataSource.addIncome(incomeToUpload.copy(synced = true))
    }

    /**
     * Updates an existing income in both local and remote data sources.
     * This function handles both online and offline cases, ensuring the income is synchronized when the user is online.
     *
     * Steps:
     * 1. If online:
     *    - If a local receipt image exists, upload it to cloud storage.
     *    - Update the income with the cloud receipt URL and clear the local image path.
     *    - Update the income in the remote data source.
     *    - Mark the income as synced and update it in the local database.
     * 2. If offline:
     *    - Update the income in the local database and mark it as unsynced.
     *    - Schedule WorkManager to sync the updated income when the network is available.
     *
     * @param income The income object that contains updated transaction details, including receipt information.
     */
    override suspend fun updateIncome(income: Income) {
        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            updateIncomeOnServer(income)
        } else {
            startUpSyncWork(income.userId)
            localIncomeDataSource.updateIncome(income.copy(synced = false))
        }
    }

    private suspend fun updateIncomeOnServer(income: Income) {
        var incomeToUpload = income
        // If the income has a local receipt image, upload it to the cloud
        if (income.localReceiptImagePath != null) {
            // Upload the image to the cloud storage and obtain the cloud URL
            val cloudUrl = storageDataSource.uploadImageToCloud(
                bucketName = "$INCOME_IMAGES/${income.id}.jpg",
                imagePath = income.localReceiptImagePath!!
            )
            if (cloudUrl != null) {
                // Update the income with the cloud receipt URL and clear the local receipt image path
                incomeToUpload = income.copy(receiptUrl = cloudUrl, localReceiptImagePath = null)
            }
        }
        // Upload the income to the remote data source (Firebase, etc.)
        remoteIncomeDataSource.updateIncome(incomeToUpload)

        // Mark the income as synced (synced = true) and save it locally
        localIncomeDataSource.updateIncome(incomeToUpload.copy(synced = true))
    }

    /**
     * Deletes an income from both local and remote data sources, handling both online and offline scenarios.
     *
     * Steps:
     * 1. Always delete the income from the local data source.
     *    - If a local receipt image path exists, delete the local image file.
     * 2. If the device is online:
     *    - Delete the receipt image from cloud storage if a receipt URL exists.
     *    - Delete the income from the remote data source (e.g., Firebase).
     * 3. If the device is offline:
     *    - Schedule WorkManager to sync the deleted income with the remote server when the device is online.
     *    - Log the deletion in a local table (e.g., `DeletedIncome`) to track items for future deletion in the remote database.
     *
     * @param income The income object to be deleted, which contains the receipt information and ID.
     */
    override suspend fun deleteIncome(income: Income) {
        val localReceiptImagePath = income.localReceiptImagePath

        // Delete the income from the local database
        localIncomeDataSource.deleteIncome(income.userId, income.id)

        // If there is a local receipt image, delete it from local storage
        if (localReceiptImagePath != null) {
            imageStorageManager.deleteImageLocally(localReceiptImagePath)
        }

        val isOnline = networkMonitor.isOnline.first()
        if (isOnline) {
            deleteIncomeOnServer(income)
        } else {
            startUpSyncWork(income.userId)
            deletedIncomeDataSource.insert(DeletedIncome(income.id, income.userId))
        }
    }

    private suspend fun deleteIncomeOnServer(income: Income) {
        // Retrieve the receipt URL and local image path from the income
        val receiptUrl = income.receiptUrl
        // If online, delete the receipt from cloud storage (if it exists)
        if (receiptUrl != null) {
            storageDataSource.deleteImageFromCloud(
                bucketName = "$INCOME_IMAGES/${income.id}.jpg",
                imagePath = receiptUrl
            )
        }
        // Delete the income from the remote database (e.g., Firebase)
        remoteIncomeDataSource.deleteIncome(income.userId, income.id)
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
                        addIncomeToServer(income)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelUpdater = {
                val updatedIncomesNotSynced = localIncomeDataSource.getNonSyncedIncomes(userId)

                updatedIncomesNotSynced.forEach { income ->
                    try {
                        updateIncomeOnServer(income)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            },
            modelDeleter = {
                val deletedIncomeIds = deletedIncomeDataSource.getDeletedIncomes(userId).map { it.incomeId }

                deletedIncomeIds.forEach { incomeId ->
                    try {
                        val income = remoteIncomeDataSource.getIncome(userId, incomeId).first()
                        if (income != null) {
                            deleteIncomeOnServer(income)
                        }
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

internal const val INCOME_IMAGES = "incomes"