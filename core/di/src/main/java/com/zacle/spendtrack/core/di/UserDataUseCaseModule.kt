package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import com.zacle.spendtrack.core.domain.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserDataUseCaseModule {
    @Provides
    fun provideGetUserDataUseCase(
        configuration: UseCase.Configuration,
        userDataRepository: UserDataRepository
    ): GetUserDataUseCase = GetUserDataUseCase(configuration, userDataRepository)
}