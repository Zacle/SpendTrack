package com.zacle.spendtrack.core.google

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.zacle.spendtrack.core.data.datasource.GoogleAuthDataSource
import com.zacle.spendtrack.core.model.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthService @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleIdOption: GetGoogleIdOption,
    private val credentialManager: CredentialManager
): GoogleAuthDataSource {
    override fun authenticateWithGoogle(context: Context): Flow<AuthResult?> = flow {
        try {
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)

            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)
                        val authResult = auth.signInWithCredential(googleCredentials).await()
                        emit(
                            AuthResult(
                                uid = authResult.user?.uid,
                                isNewUser = authResult.additionalUserInfo?.isNewUser,
                                displayName = authResult.user?.displayName,
                                providerId = authResult.additionalUserInfo?.providerId,
                                photoUrl = authResult.user?.photoUrl,
                                email = authResult.user?.email
                            )
                        )
                    } else {
                        emit(null)
                    }
                }
                else -> emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }
}