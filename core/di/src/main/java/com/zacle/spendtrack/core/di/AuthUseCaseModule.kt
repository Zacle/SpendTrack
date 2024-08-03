package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase
import com.zacle.spendtrack.core.domain.repository.AuthStateUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {
    @Provides
    fun provideObserveUserAuthStateUseCase(
        configuration: UseCase.Configuration,
        authStateUserRepository: AuthStateUserRepository
    ): ObserveUserAuthStateUseCase = ObserveUserAuthStateUseCase(configuration, authStateUserRepository)
}