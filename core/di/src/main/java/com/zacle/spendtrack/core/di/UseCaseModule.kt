package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.domain.UseCase
import com.zacle.spendtrack.core.domain.GetUserDataAndAuthStateUseCase
import com.zacle.spendtrack.core.domain.auth.ObserveUserAuthStateUseCase
import com.zacle.spendtrack.core.domain.datastore.GetUserDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideUseCaseConfiguration(
        @STDispatcher(IO) ioDispatcher: CoroutineDispatcher
    ): UseCase.Configuration = UseCase.Configuration(ioDispatcher)

    @Provides
    fun provideUserDataAndAuthStateUseCase(
        configuration: UseCase.Configuration,
        getUserDataUseCase: GetUserDataUseCase,
        observeUserAuthStateUseCase: ObserveUserAuthStateUseCase
    ): GetUserDataAndAuthStateUseCase =
        GetUserDataAndAuthStateUseCase(configuration, getUserDataUseCase, observeUserAuthStateUseCase)
}