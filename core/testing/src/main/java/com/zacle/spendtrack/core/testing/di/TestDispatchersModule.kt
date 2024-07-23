package com.zacle.spendtrack.core.testing.di

import com.zacle.spendtrack.core.common.SpendTrackDispatcher
import com.zacle.spendtrack.core.common.SpendTrackDispatchers
import com.zacle.spendtrack.core.common.di.DispatchersModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatchersModule::class],
)
internal object TestDispatchersModule {
    @Provides
    @SpendTrackDispatcher(SpendTrackDispatchers.IO)
    fun providesIODispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    @Provides
    @SpendTrackDispatcher(SpendTrackDispatchers.Default)
    fun providesDefaultDispatcher(
        testDispatcher: TestDispatcher,
    ): CoroutineDispatcher = testDispatcher
}