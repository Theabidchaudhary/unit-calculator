package com.orwyx.unitcalculator.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val METER_ALERTS = "meter_alerts"
    const val CYCLE_ALERTS = "cycle_alerts"

    fun createAll(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                METER_ALERTS,
                "Meter Switch Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Alerts when it's time to switch to the next electricity meter"
            },
        )
        nm.createNotificationChannel(
            NotificationChannel(
                CYCLE_ALERTS,
                "Billing Cycle Alerts",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Reminders when the billing cycle ends and meters need resetting"
            },
        )
    }
}
