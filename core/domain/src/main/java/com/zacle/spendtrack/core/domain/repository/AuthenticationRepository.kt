package com.zacle.spendtrack.core.domain.repository

import android.content.Context
import com.zacle.spendtrack.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository for handling user authentication operations.
 */
interface AuthenticationRepository {
    suspend fun authenticateWithGoogle(context: Context): Flow<Boolean>
    suspend fun signInWithEmailAndPassword(email: String, password: String)
    suspend fun signUpWithEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean
    suspend fun signOut()
}