package com.zacle.spendtrack.core.common.di

import android.content.Context
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers
import com.zacle.spendtrack.core.common.util.ConnectivityManagerNetworkMonitor
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.common.util.TimeZoneBroadcastMonitor
import com.zacle.spendtrack.core.common.util.TimeZoneMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    @Provides
    @Singleton
    fun provideTimeZoneMonitor(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): TimeZoneMonitor = TimeZoneBroadcastMonitor(context, scope, ioDispatcher)

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context,
        @STDispatcher(STDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): NetworkMonitor = ConnectivityManagerNetworkMonitor(context, ioDispatcher)
}