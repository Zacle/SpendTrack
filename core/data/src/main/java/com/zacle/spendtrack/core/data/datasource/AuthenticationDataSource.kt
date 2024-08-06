package com.zacle.spendtrack.core.data.datasource

import com.zacle.spendtrack.core.model.auth.AuthResult

interface AuthenticationDataSource {
    suspend fun signUpWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signInWithEmailAndPassword(email: String, password: String): Boolean
    suspend fun sendEmailVerification(): Boolean
    suspend fun updatePassword(password: String): Boolean
    suspend fun sendPasswordResetEmail(email: String): Boolean
    suspend fun reloadUser(): Boolean
    suspend fun signOut()
}