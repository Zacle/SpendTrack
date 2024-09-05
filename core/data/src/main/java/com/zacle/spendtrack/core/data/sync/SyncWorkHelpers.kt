package com.zacle.spendtrack.core.data.sync

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.zacle.spendtrack.core.data.R

private const val BUDGET_SYNC_NOTIFICATION_ID = 0
private const val EXPENSE_SYNC_NOTIFICATION_ID = 1
private const val INCOME_SYNC_NOTIFICATION_ID = 2
private const val BUDGET_SYNC_NOTIFICATION_CHANNEL_ID = "budget_sync_notification_channel"
private const val EXPENSE_SYNC_NOTIFICATION_CHANNEL_ID = "expense_sync_notification_channel"
private const val INCOME_SYNC_NOTIFICATION_CHANNEL_ID = "income_sync_notification_channel"

val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

fun Context.syncBudgetForegroundInfo() = ForegroundInfo(
    BUDGET_SYNC_NOTIFICATION_ID,
    syncWorkNotification(
        channelId = BUDGET_SYNC_NOTIFICATION_CHANNEL_ID,
        channelNameResId = R.string.budget_sync_work_notification_channel_name,
        channelDescriptionResId = R.string.budget_sync_work_notification_channel_description,
        titleResId = R.string.budget_sync_work_notification_title
    )
)

fun Context.syncExpenseForegroundInfo() = ForegroundInfo(
    EXPENSE_SYNC_NOTIFICATION_ID,
    syncWorkNotification(
        channelId = EXPENSE_SYNC_NOTIFICATION_CHANNEL_ID,
        channelNameResId = R.string.expense_sync_work_notification_channel_name,
        channelDescriptionResId = R.string.expense_sync_work_notification_channel_description,
        titleResId = R.string.expense_sync_work_notification_title
    )
)

fun Context.syncIncomeForegroundInfo() = ForegroundInfo(
    INCOME_SYNC_NOTIFICATION_ID,
    syncWorkNotification(
        channelId = INCOME_SYNC_NOTIFICATION_CHANNEL_ID,
        channelNameResId = R.string.income_sync_work_notification_channel_name,
        channelDescriptionResId = R.string.income_sync_work_notification_channel_description,
        titleResId = R.string.income_sync_work_notification_title
    )
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.syncWorkNotification(
    channelId: String,
    channelNameResId: Int,
    channelDescriptionResId: Int,
    titleResId: Int
): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            getString(channelNameResId),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(channelDescriptionResId)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.notification_important)
        .setContentTitle(getString(titleResId))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}