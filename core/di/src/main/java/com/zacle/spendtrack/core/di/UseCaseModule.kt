package com.zacle.spendtrack.core.di

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
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
        @STDispatcher(IO) ioDispatcher: CoroutineDispatcher
    ): UseCase.Configuration = UseCase.Configuration(ioDispatcher)
}