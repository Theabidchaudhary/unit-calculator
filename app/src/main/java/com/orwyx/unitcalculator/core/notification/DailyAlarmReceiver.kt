package com.orwyx.unitcalculator.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.orwyx.unitcalculator.core.worker.DailyMeterCheckWorker

/**
 * Receives AlarmManager broadcasts for the daily 12PM check and "Not yet" reminders.
 * No Hilt injection here — heavy work is delegated to DailyMeterCheckWorker via WorkManager.
 */
class DailyAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            MeterAlarmScheduler.ACTION_DAILY_CHECK -> {
                // Reschedule for the next day before doing work (survive crashes)
                MeterAlarmScheduler.scheduleNextDay(context)
                enqueueWorker(context)
            }
            MeterAlarmScheduler.ACTION_REMINDER -> {
                val meterId = intent.getLongExtra(MeterAlertReceiver.EXTRA_METER_ID, -1L)
                enqueueWorker(context, isReminder = true, meterId = meterId)
            }
        }
    }

    private fun enqueueWorker(context: Context, isReminder: Boolean = false, meterId: Long = -1L) {
        val data = workDataOf(
            DailyMeterCheckWorker.KEY_IS_REMINDER to isReminder,
            DailyMeterCheckWorker.KEY_METER_ID    to meterId,
        )
        val request = OneTimeWorkRequestBuilder<DailyMeterCheckWorker>()
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
