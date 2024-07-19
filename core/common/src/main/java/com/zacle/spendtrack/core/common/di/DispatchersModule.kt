package com.zacle.spendtrack.core.common.di

import com.zacle.spendtrack.core.common.SpendTrackDispatcher
import com.zacle.spendtrack.core.common.SpendTrackDispatchers.Default
import com.zacle.spendtrack.core.common.SpendTrackDispatchers.IO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @SpendTrackDispatcher(Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @SpendTrackDispatcher(IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}