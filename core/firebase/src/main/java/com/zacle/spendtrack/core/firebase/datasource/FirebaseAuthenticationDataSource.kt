package com.zacle.spendtrack.core.firebase.datasource

import com.google.firebase.auth.FirebaseAuth
import com.zacle.spendtrack.core.data.datasource.AuthenticationDataSource
import com.zacle.spendtrack.core.model.auth.AuthResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthenticationDataSource @Inject constructor(
    private val auth: FirebaseAuth
): AuthenticationDataSource {
    override suspend fun signUpWithEmailAndPassword(email: String, password: String): AuthResult {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        return AuthResult(
            uid = authResult.user?.uid,
            isNewUser = authResult.additionalUserInfo?.isNewUser,
            displayName = authResult.user?.displayName,
            providerId = authResult.additionalUserInfo?.providerId,
            photoUrl = authResult.user?.photoUrl,
            email = authResult.user?.email,
            isEmailVerified = authResult.user?.isEmailVerified
        )
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return AuthResult(
            uid = authResult.user?.uid,
            isNewUser = authResult.additionalUserInfo?.isNewUser,
            displayName = authResult.user?.displayName,
            providerId = authResult.additionalUserInfo?.providerId,
            photoUrl = authResult.user?.photoUrl,
            email = authResult.user?.email,
            isEmailVerified = authResult.user?.isEmailVerified
        )
    }

    override suspend fun sendEmailVerification(): Boolean =
        try {
            val currentUser = auth.currentUser
            currentUser?.sendEmailVerification()?.await()
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun reloadUser(): Boolean =
        try {
            auth.currentUser?.reload()?.await()
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun updatePassword(password: String): Boolean =
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                false
            } else {
                currentUser.updatePassword(password).await()
                true
            }
        } catch (e: Exception) {
            false
        }


    override suspend fun sendPasswordResetEmail(email: String): Boolean =
        try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }

    override suspend fun signOut() {
        auth.signOut()
    }
}