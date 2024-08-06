package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Entry point for the user data sources. We have two data sources:
 *  - Local data source using RoomDB
 *  - Remote data source using Firestore
 *
 *  The default data source to retrieve the user is the local data source.
 */
interface UserDataSource {
    /**
     * Get the current logged in user
     */
    suspend fun getUser(userId: String): Flow<User?>

    /**
     * Update the current logged in user
     */
    suspend fun updateUser(user: User)

    /**
     * Create a user
     */
    suspend fun insertUser(user: User)


    /**
     * Delete the current logged in user
     */
    suspend fun deleteUser(userId: String)
}