package com.zacle.spendtrack.core.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.common.util.ImageStorageManager
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.data.datasource.StorageDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.data.sync.SyncConstraints
import com.zacle.spendtrack.core.data.sync.SyncUserWorker
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.domain.repository.UserRepository
import com.zacle.spendtrack.core.model.User
import com.zacle.spendtrack.core.model.util.Synchronizer
import com.zacle.spendtrack.core.model.util.changeLastSyncTimes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class OfflineFirstUserRepository @Inject constructor(
    @LocalUserData private val localUserDataSource: UserDataSource,
    @RemoteUserData private val remoteUserDataSource: UserDataSource,
    @ApplicationContext private val context: Context,
    private val authStateUserRepository: AuthStateUserRepository,
    private val networkMonitor: NetworkMonitor,
    private val storageDataSource: StorageDataSource,
    private val imageStorageManager: ImageStorageManager
): UserRepository {
    override suspend fun getUser(): Flow<User?> =
        authStateUserRepository.userInfo.flatMapLatest { userInfo ->
            if (userInfo == null || !userInfo.isSignedIn()) {
                return@flatMapLatest flow { emit(null) }
            }
            val userId = userInfo.getUserId() ?: return@flatMapLatest flow { emit(null) }
            val isOnline = networkMonitor.isOnline.first()

            // Fetch from local data source first
            localUserDataSource.getUser(userId).flatMapLatest { localUser ->
                if (localUser == null && isOnline) {
                    // Fetch from remote if local is null and online
                    remoteUserDataSource.getUser(userId).flatMapLatest { remoteUser ->
                        if (remoteUser != null) {
                            // Save remote user to local data source
                            localUserDataSource.insertUser(remoteUser)
                        }
                        flow { emit(remoteUser) }
                    }
                } else {
                    // Emit the local user directly
                    flow { emit(localUser) }
                }
            }
        }

    /**
     * Updates an existing user in both local and remote data sources.
     * This function handles both online and offline cases, ensuring the user is synchronized when the he is online.
     *
     * Steps:
     * 1. If online:
     *    - If a local receipt image exists, upload it to cloud storage.
     *    - Update the user with the cloud receipt URL and clear the local image path.
     *    - Update the user in the remote data source.
     *    - Mark the user as synced and update it in the local database.
     * 2. If offline:
     *    - Update the user in the local database and mark it as unsynced.
     *    - Schedule WorkManager to sync the updated user when the network is available.
     *
     * @param user The user object that contains updated user details, including profile picture URL information.
     */
    override suspend fun updateUser(user: User) {
        // Check the current network status (online or offline)
        val isOnline = networkMonitor.isOnline.first()

        if (isOnline) {
            updateUserOnServer(user)
        } else {
            localUserDataSource.updateUser(user.copy(synced = false))
            // Schedule WorkManager to sync the updated user when the device goes online
            startUpSyncWork(user.userId)
        }
    }

    private suspend fun updateUserOnServer(user: User) {
        var userToUpload = user
        val localReceiptImagePath = user.localProfilePictureUrl

        // If the user has a local receipt image, upload it to cloud storage
        if (localReceiptImagePath != null) {
            // Upload the image to the cloud storage and get the cloud URL
            val cloudUrl = storageDataSource.uploadImageToCloud(
                bucketName = "$USER_IMAGES/${user.userId}.jpg",
                imagePath = localReceiptImagePath
            )
            // If the upload is successful, update the user with the cloud receipt URL
            if (cloudUrl != null) {
                userToUpload = user.copy(profilePictureUrl = cloudUrl, localProfilePictureUrl = null)
                // Delete the local image file after uploading to avoid storage quota errors
                imageStorageManager.deleteImageLocally(localReceiptImagePath)
            }
        }
        // Update the user in the remote data source (e.g., Firebase)
        remoteUserDataSource.updateUser(userToUpload)

        // Mark the user as synced and update it in the local database
        localUserDataSource.updateUser(userToUpload.copy(synced = true))
    }

    private fun startUpSyncWork(userId: String) {
        val inputData = Data.Builder()
            .putString(USER_ID_KEY, userId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncUserWorker>()
            .setConstraints(SyncConstraints)
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val operation = WorkManager.getInstance(context)
            .enqueueUniqueWork(
                USERS_SYNC_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            .result

        operation.addListener(
            { Timber.i("SyncUserWorker enqueued") },
            { it.run() }
        )
    }

    override suspend fun syncWith(userId: String, synchronizer: Synchronizer): Boolean =
        synchronizer.changeLastSyncTimes(
            lastSyncUpdater = { copy(userLastSync = it)},
            modelUpdater = {
                val user = localUserDataSource.getUser(userId).first()
                if (user != null) {
                    if (!user.synced) {
                        updateUserOnServer(user)
                    }
                }
            },
            modelAdder = {},
            modelDeleter = {}
        )
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val USERS_SYNC_WORK_NAME = "UserSyncWorkName"

internal const val USER_IMAGES = "users"