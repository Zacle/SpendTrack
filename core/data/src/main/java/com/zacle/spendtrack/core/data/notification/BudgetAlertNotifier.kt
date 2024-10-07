package com.zacle.spendtrack.core.data.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zacle.spendtrack.core.common.notification.Notifier
import com.zacle.spendtrack.core.shared_resources.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BudgetAlertNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
): Notifier {
    private val notificationManager = NotificationManagerCompat.from(context)

    override fun createBaseNotification(
        channelId: String,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification {
        ensureNotificationChannelsExist()
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.budget)
            .setSilent(false)
            .setAutoCancel(true)
            .apply(block)
            .build()
    }

    override fun ensureNotificationChannelsExist() {
        // Check if the device is running Android Oreo (API 26) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BUDGET_ALERT_CHANNEL_ID,
                context.getString(R.string.budget_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.budget_channel_name)
            }

            // Register the channel with the system
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    fun showBudgetAlertNotification(isBudgetExceeded: Boolean) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val descriptionResId =
            if (isBudgetExceeded) R.string.budget_channel_exceeded_description
            else R.string.budget_channel_remaining_description

        val notification = createBaseNotification(BUDGET_ALERT_CHANNEL_ID) {
            setContentTitle(getString(R.string.budget_channel_name))
            setContentText(getString(descriptionResId))
            setStyle(NotificationCompat.BigTextStyle().bigText(getString(descriptionResId)))
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

        notificationManager.notify(BUDGET_ALERT_NOTIFICATION_ID, notification)
    }
}

private const val BUDGET_ALERT_CHANNEL_ID = "budget_alert_channel_id"
private const val BUDGET_ALERT_NOTIFICATION_ID = 13