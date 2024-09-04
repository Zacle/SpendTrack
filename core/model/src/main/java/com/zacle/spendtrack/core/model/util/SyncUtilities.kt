package com.zacle.spendtrack.core.model.util

import com.zacle.spendtrack.core.model.ChangeLastSyncTimes
import kotlinx.datetime.Clock
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 */
interface Synchronizer {
    suspend fun getChangeLastSyncTimes(): ChangeLastSyncTimes

    suspend fun updateChangeLastSyncTimes(update: ChangeLastSyncTimes.() -> ChangeLastSyncTimes)

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
     */
    suspend fun Syncable.sync(userId: String) = this@sync.syncWith(userId, this@Synchronizer)
}

/**
 * Interface marker for a class that is synchronized with a remote source. Syncing must not be
 * performed concurrently and it is the [Synchronizer]'s responsibility to ensure this.
 */
interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     */
    suspend fun syncWith(userId: String, synchronizer: Synchronizer): Boolean
}

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Timber.i(
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result"
    )
    Result.failure(exception)
}

suspend fun Synchronizer.changeLastSyncTimes(
    lastSyncUpdater: ChangeLastSyncTimes.(Long) -> ChangeLastSyncTimes,
    modelAdder: suspend () -> Unit,
    modelUpdater: suspend () -> Unit,
    modelDeleter: suspend () -> Unit
) = suspendRunCatching {
    modelAdder()
    modelUpdater()
    modelDeleter()
    updateChangeLastSyncTimes {
        lastSyncUpdater(Clock.System.now().toEpochMilliseconds())
    }
}.isSuccess