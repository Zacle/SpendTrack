package com.zacle.spendtrack.core.testing.di

import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.Default
import com.zacle.spendtrack.core.common.di.ApplicationScope
import com.zacle.spendtrack.core.common.di.CoroutineScopeModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.TestScope
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutineScopeModule::class]
)
object TestCoroutineScopeModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun provideCoroutineScope(
        @STDispatcher(Default) dispatcher: CoroutineDispatcher
    ): CoroutineScope = TestScope(SupervisorJob() + dispatcher)
}