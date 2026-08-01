package com.orwyx.unitcalculator.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object MeterAlarmScheduler {

    const val ACTION_DAILY_CHECK = "com.orwyx.unitcalculator.ACTION_DAILY_CHECK"
    const val ACTION_REMINDER    = "com.orwyx.unitcalculator.ACTION_REMINDER"

    private const val REQUEST_DAILY    = 1000
    private const val REQUEST_REMINDER = 1001

    /**
     * Schedule the daily 12PM alarm. If 12PM has already passed today, schedules for tomorrow.
     * Calling this multiple times is safe — it replaces any existing alarm.
     */
    fun schedule(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = buildDailyIntent(context)
        am.cancel(pi)
        setAlarm(am, nextNoonMillis(skipToTomorrow = false), pi)
    }

    /** Schedules the NEXT day's 12PM alarm. Call from inside DailyAlarmReceiver after firing. */
    fun scheduleNextDay(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setAlarm(am, nextNoonMillis(skipToTomorrow = true), buildDailyIntent(context))
    }

    /** Schedule a one-shot reminder [delayMs] milliseconds from now. */
    fun scheduleReminder(context: Context, meterId: Long, delayMs: Long = 60 * 60 * 1000L) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyAlarmReceiver::class.java).apply {
            action = ACTION_REMINDER
            putExtra(MeterAlertReceiver.EXTRA_METER_ID, meterId)
        }
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_REMINDER, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        setAlarm(am, System.currentTimeMillis() + delayMs, pi)
    }

    private fun setAlarm(am: AlarmManager, triggerAtMillis: Long, pi: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            // Fallback: not exact but still wakes device in Doze (may be delayed ~1h by OS)
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        }
    }

    private fun buildDailyIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyAlarmReceiver::class.java).apply {
            action = ACTION_DAILY_CHECK
        }
        return PendingIntent.getBroadcast(
            context, REQUEST_DAILY, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextNoonMillis(skipToTomorrow: Boolean): Long {
        val cal = Calendar.getInstance()
        if (skipToTomorrow || cal.get(Calendar.HOUR_OF_DAY) >= 12) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        cal.set(Calendar.HOUR_OF_DAY, 12)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
