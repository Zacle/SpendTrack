package com.zacle.spendtrack.core.common.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.zacle.spendtrack.core.common.STDispatcher
import com.zacle.spendtrack.core.common.STDispatchers.IO
import com.zacle.spendtrack.core.common.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import java.time.ZoneId
import javax.inject.Inject

/**
 * Utility for reporting current timezone the device has set.
 * It always emits at least once with default setting and then for each TZ change.
 */
interface TimeZoneMonitor {
    val currentTimeZone: Flow<TimeZone>
}

class TimeZoneBroadcastMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope,
    @STDispatcher(IO) private val ioDispatcher: CoroutineDispatcher
): TimeZoneMonitor {
    override val currentTimeZone: SharedFlow<TimeZone> = callbackFlow {
        // Send the default time zone first
        trySend(TimeZone.currentSystemDefault())

        // Registers BroadcastReceiver for the TimeZone changes
        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != Intent.ACTION_TIMEZONE_CHANGED) return

                val zoneIdFromIntent = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    null
                } else {
                    // Starting Android R we also get the new TimeZone.
                    intent.getStringExtra(Intent.EXTRA_TIMEZONE)?.let { timeZoneId ->
                        // We need to convert it from java.util.Timezone to java.time.ZoneId
                        val zoneId = ZoneId.of(timeZoneId, ZoneId.SHORT_IDS)
                        // Convert to kotlinx.datetime.TimeZone
                        zoneId.toKotlinTimeZone()
                    }
                }

                // If there isn't a zoneId in the intent, fallback to the systemDefault, which should also reflect the change
                trySend(zoneIdFromIntent ?: TimeZone.currentSystemDefault())
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))

        // Send here again, because registering the Broadcast Receiver can take up to several milliseconds.
        // This way, we can reduce the likelihood that a TZ change wouldn't be caught with the Broadcast Receiver.
        trySend(TimeZone.currentSystemDefault())

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
        .distinctUntilChanged()
        .conflate()
        .flowOn(ioDispatcher)
        .shareIn(scope, SharingStarted.WhileSubscribed())
}