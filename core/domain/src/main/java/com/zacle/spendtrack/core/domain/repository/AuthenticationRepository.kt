package com.zacle.spendtrack.core.domain.repository

import android.content.Context
import com.zacle.spendtrack.core.model.User
import com.zacle.spendtrack.core.model.auth.AuthResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository for handling user authentication operations.
 */
interface AuthenticationRepository {
    suspend fun authenticateWithGoogle(context: Context): Flow<AuthResult>
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signUpWithEmailAndPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): AuthResult
    suspend fun updatePassword(password: String): Boolean
    suspend fun sendPasswordResetEmail(email: String): Boolean
    suspend fun signOut()
}