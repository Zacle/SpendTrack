package com.zacle.spendtrack.core.google.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.zacle.spendtrack.core.data.datasource.GoogleAuthDataSource
import com.zacle.spendtrack.core.google.BuildConfig.WEB_CLIENT_ID
import com.zacle.spendtrack.core.google.GoogleAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleAuthModule {
    @Provides
    @Singleton
    fun provideGetGoogleIdOption(): GetGoogleIdOption {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val nonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setNonce(nonce)
            .setServerClientId(WEB_CLIENT_ID)
            .build()
    }

    @Provides
    @Singleton
    fun provideCredentialManager(
        @ApplicationContext context: Context,
    ): CredentialManager = CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideGoogleAuthDataSource(
        credentialManager: CredentialManager,
        getGoogleIdOption: GetGoogleIdOption,
        firebaseAuth: FirebaseAuth
    ): GoogleAuthDataSource = GoogleAuthService(firebaseAuth, getGoogleIdOption, credentialManager)
}