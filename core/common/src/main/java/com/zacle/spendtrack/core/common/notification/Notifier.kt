package com.zacle.spendtrack.core.common.notification

import android.app.Notification
import androidx.core.app.NotificationCompat

interface Notifier {
    fun createBaseNotification(
        channelId: String,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification

    fun ensureNotificationChannelsExist()
}