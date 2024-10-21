package com.zacle.spendtrack.core.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.zacle.spendtrack.core.data.Constants.USER_ID_KEY
import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.domain.repository.UserRepository
import com.zacle.spendtrack.core.model.ChangeLastSyncTimes
import com.zacle.spendtrack.core.model.util.Synchronizer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class SyncUserWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val userPreferencesDataSource: UserPreferencesDataSource,
    private val userRepository: UserRepository,
    private val ioDispatcher: CoroutineDispatcher
): CoroutineWorker(context, workerParams), Synchronizer {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        context.syncUserForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            // Get the user's ID
            val userId = inputData.getString(USER_ID_KEY)
                ?: throw IllegalArgumentException("Missing userId argument")

            // Sync the data
            val syncedSuccessfully = userRepository.sync(userId)

            if (syncedSuccessfully) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure()
        }
    }

    override suspend fun getChangeLastSyncTimes(): ChangeLastSyncTimes =
        userPreferencesDataSource.getChangeLastSyncTimes()

    override suspend fun updateChangeLastSyncTimes(update: ChangeLastSyncTimes.() -> ChangeLastSyncTimes) =
        userPreferencesDataSource.updateChangeLastSyncTimes(update)

    class Factory(
        private val userRepository: UserRepository,
        private val userPreferencesDataSource: UserPreferencesDataSource,
        private val ioDispatcher: CoroutineDispatcher
    ): WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? =
            if (workerClassName == SyncUserWorker::class.java.name) {
                SyncUserWorker(
                    context = appContext,
                    workerParams = workerParameters,
                    userPreferencesDataSource = userPreferencesDataSource,
                    userRepository = userRepository,
                    ioDispatcher = ioDispatcher
                )
            } else {
                null
            }
    }
}