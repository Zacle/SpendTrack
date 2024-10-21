package com.zacle.spendtrack.core.domain.repository

import com.zacle.spendtrack.core.model.User
import com.zacle.spendtrack.core.model.util.Syncable
import kotlinx.coroutines.flow.Flow

/**
 * Repository for handling user data operations.
 *
 * It saves data on both data sources: local and remote.
 *
 * If the user is not connected to the internet, it schedules the sync through a WorkManager
 * to be saved later in the remote
 */
interface UserRepository: Syncable {
    /**
     * Get the current logged in user
     */
    suspend fun getUser(): Flow<User?>

    /**
     * Update the current logged in user
     */
    suspend fun updateUser(user: User)
}