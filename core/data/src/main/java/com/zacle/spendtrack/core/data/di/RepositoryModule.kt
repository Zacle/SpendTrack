package com.zacle.spendtrack.core.data.di

import com.zacle.spendtrack.core.common.di.LocalUserData
import com.zacle.spendtrack.core.common.di.RemoteUserData
import com.zacle.spendtrack.core.data.datasource.AuthStateUserDataSource
import com.zacle.spendtrack.core.data.datasource.AuthenticationDataSource
import com.zacle.spendtrack.core.data.datasource.GoogleAuthDataSource
import com.zacle.spendtrack.core.data.datasource.UserDataSource
import com.zacle.spendtrack.core.data.repository.DefaultAuthStateUserRepository
import com.zacle.spendtrack.core.data.repository.DefaultAuthenticationRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstUserDataRepository
import com.zacle.spendtrack.core.datastore.UserPreferencesDataSource
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun  provideUserDataRepository(
        userPreferencesDataSource: UserPreferencesDataSource
    ): UserDataRepository = OfflineFirstUserDataRepository(userPreferencesDataSource)

    @Provides
    @Singleton
    fun provideAuthStateUserRepository(
        authStateUserDataSource: AuthStateUserDataSource
    ): AuthStateUserRepository = DefaultAuthStateUserRepository(authStateUserDataSource)

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        authenticationDataSource: AuthenticationDataSource,
        googleAuthDataSource: GoogleAuthDataSource,
        @RemoteUserData remoteUserDataSource: UserDataSource,
        @LocalUserData localUserDataSource: UserDataSource
    ): AuthenticationRepository =
        DefaultAuthenticationRepository(
            authenticationDataSource,
            googleAuthDataSource,
            remoteUserDataSource,
            localUserDataSource
        )
}