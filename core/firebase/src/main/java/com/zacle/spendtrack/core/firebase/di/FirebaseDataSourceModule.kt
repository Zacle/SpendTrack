package com.zacle.spendtrack.core.firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zacle.spendtrack.core.common.di.ApplicationScope
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.data.datasource.AuthenticationDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseAuthStateUserDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseAuthenticationDataSource
import com.zacle.spendtrack.core.firebase.datasource.FirebaseUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseDataSourceModule {
    @Provides
    @Singleton
    fun provideAuthStateUserDataSource(
        auth: FirebaseAuth,
        @ApplicationScope scope: CoroutineScope
    ): AuthStateUserDataSource = FirebaseAuthStateUserDataSource(auth, scope)

    @Provides
    @Singleton
    @RemoteUserData
    fun provideUserDataSource(
        firestore: FirebaseFirestore
    ): UserDataSource = FirebaseUserDataSource(firestore)

    @Provides
    @Singleton
    fun provideAuthenticationDataSource(
        auth: FirebaseAuth
    ): AuthenticationDataSource = FirebaseAuthenticationDataSource(auth)
}