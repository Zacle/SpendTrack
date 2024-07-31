package com.zacle.spendtrack.core.common.di

import com.zacle.spendtrack.core.common.util.ConnectivityManagerNetworkMonitor
import com.zacle.spendtrack.core.common.util.NetworkMonitor
import com.zacle.spendtrack.core.common.util.TimeZoneBroadcastMonitor
import com.zacle.spendtrack.core.common.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilModule {
    @Binds
    abstract fun bindTimeZoneMonitor(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor

    @Binds
    abstract fun bindNetworkMonitor(impl: ConnectivityManagerNetworkMonitor): NetworkMonitor

}