package com.orwyx.unitcalculator.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Reschedules the daily alarm after device reboot, because AlarmManager alarms don't survive reboots. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            MeterAlarmScheduler.schedule(context)
        }
    }
}
