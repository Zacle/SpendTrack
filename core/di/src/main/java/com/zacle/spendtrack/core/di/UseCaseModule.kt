package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.common.SpendTrackDispatcher
import com.zacle.spendtrack.core.common.SpendTrackDispatchers.IO
import com.zacle.spendtrack.core.domain.UseCase
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
        @SpendTrackDispatcher(IO) ioDispatcher: CoroutineDispatcher
    ): UseCase.Configuration = UseCase.Configuration(ioDispatcher)
}