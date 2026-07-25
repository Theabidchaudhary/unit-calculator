package com.orwyx.unitcalculator.core.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import javax.inject.Inject

class MeterAlertNotifier @Inject constructor(
    private val context: Context,
) {
    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun sendSwitchTomorrowNotification(
        activeMeterName: String,
        nextMeterName: String?,
        remainingUnits: Double,
    ) {
        val nextText = if (nextMeterName != null) "Next: $nextMeterName" else ""
        val builder = NotificationCompat.Builder(context, NotificationChannels.METER_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Switch meter tomorrow")
            .setContentText("$activeMeterName has ≈${"%.0f".format(remainingUnits)} units left. $nextText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        nm.notify(NOTIF_ID_SWITCH_TOMORROW, builder.build())
    }

    fun sendSwitchTodayNotification(
        activeMeterName: String,
        activMeterId: Long,
        nextMeterName: String?,
    ) {
        val nextText = if (nextMeterName != null) " Switch to $nextMeterName." else ""
        val yesIntent = pendingBroadcast(MeterAlertReceiver.ACTION_SWITCH_YES, activMeterId)
        val noIntent = pendingBroadcast(MeterAlertReceiver.ACTION_SWITCH_NO, activMeterId)

        val builder = NotificationCompat.Builder(context, NotificationChannels.METER_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Time to switch meters")
            .setContentText("$activeMeterName has reached its limit.$nextText Have you switched?")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .addAction(0, "Yes, switched", yesIntent)
            .addAction(0, "Not yet", noIntent)
        nm.notify(NOTIF_ID_SWITCH_TODAY, builder.build())
    }

    fun sendCycleEndNotification() {
        val resetIntent = pendingBroadcast(MeterAlertReceiver.ACTION_RESET_ALL, 0L)
        val builder = NotificationCompat.Builder(context, NotificationChannels.CYCLE_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Billing cycle ended")
            .setContentText("Tap Reset All to archive readings and start the new cycle from meter #1.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(0, "Reset All", resetIntent)
        nm.notify(NOTIF_ID_CYCLE_END, builder.build())
    }

    fun cancelSwitchToday() = nm.cancel(NOTIF_ID_SWITCH_TODAY)
    fun cancelSwitchTomorrow() = nm.cancel(NOTIF_ID_SWITCH_TOMORROW)

    private fun pendingBroadcast(action: String, meterId: Long): PendingIntent {
        val intent = Intent(context, MeterAlertReceiver::class.java).apply {
            this.action = action
            putExtra(MeterAlertReceiver.EXTRA_METER_ID, meterId)
        }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val NOTIF_ID_SWITCH_TOMORROW = 1001
        const val NOTIF_ID_SWITCH_TODAY = 1002
        const val NOTIF_ID_CYCLE_END = 1003
    }
}
