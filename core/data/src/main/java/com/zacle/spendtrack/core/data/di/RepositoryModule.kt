package com.zacle.spendtrack.core.data.di

import com.zacle.spendtrack.core.data.repository.DefaultAuthStateUserRepository
import com.zacle.spendtrack.core.data.repository.DefaultAuthenticationRepository
import com.zacle.spendtrack.core.data.repository.OfflineFirstUserDataRepository
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import com.zacle.spendtrack.core.domain.repository.AuthenticationRepository
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository
    ): UserDataRepository

    @Binds
    abstract fun bindAuthStateUserRepository(
        authStateUserRepository: DefaultAuthStateUserRepository
    ): AuthStateUserRepository

    @Binds
    abstract fun bindAuthenticationRepository(
        authenticationRepository: DefaultAuthenticationRepository
    ): AuthenticationRepository
}