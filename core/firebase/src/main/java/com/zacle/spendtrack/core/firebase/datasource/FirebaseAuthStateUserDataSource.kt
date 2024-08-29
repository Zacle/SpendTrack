package com.zacle.spendtrack.core.firebase.datasource

import com.google.firebase.auth.FirebaseAuth
import com.zacle.spendtrack.core.common.di.ApplicationScope
import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.firebase.model.FirebaseUserInfo
import com.zacle.spendtrack.core.model.auth.AuthenticatedUserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

/**
 * An [AuthStateUserDataSource] that listens to changes in [FirebaseAuth]
 */
class FirebaseAuthStateUserDataSource(
    private val auth: FirebaseAuth,
    @ApplicationScope private val appScope: CoroutineScope
): AuthStateUserDataSource {

    override fun getUserInfo(): SharedFlow<AuthenticatedUserInfo> =
        callbackFlow {
            val authListener: ((FirebaseAuth) -> Unit) = { auth ->
                trySend(auth)
            }
            auth.addAuthStateListener(authListener)
            awaitClose { auth.removeAuthStateListener(authListener) }
        }
        .map { authState ->
            authState.currentUser?.reload()
            if (authState.currentUser != null && authState.currentUser?.isEmailVerified == false) {
                authState.currentUser?.sendEmailVerification()
            }
            FirebaseUserInfo(authState.currentUser)
        }
        .shareIn(appScope, SharingStarted.WhileSubscribed(), replay = 1)
}