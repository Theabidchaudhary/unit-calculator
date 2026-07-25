package com.orwyx.unitcalculator.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.orwyx.unitcalculator.core.worker.DailyMeterCheckWorker
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MeterAlertReceiver : BroadcastReceiver() {

    @Inject lateinit var meterRepository: MeterRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var notifier: MeterAlertNotifier

    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        val meterId = intent.getLongExtra(EXTRA_METER_ID, -1L)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    ACTION_SWITCH_YES -> {
                        if (meterId > 0) meterRepository.lockMeter(meterId)
                        notifier.cancelSwitchToday()
                    }
                    ACTION_SWITCH_NO -> {
                        notifier.cancelSwitchToday()
                        // Schedule another reminder in 1 hour
                        val reminder = OneTimeWorkRequestBuilder<DailyMeterCheckWorker>()
                            .setInitialDelay(1, TimeUnit.HOURS)
                            .setInputData(workDataOf(
                                DailyMeterCheckWorker.KEY_IS_REMINDER to true,
                                DailyMeterCheckWorker.KEY_METER_ID to meterId,
                            ))
                            .addTag(DailyMeterCheckWorker.TAG_SWITCH_REMINDER)
                            .build()
                        WorkManager.getInstance(context).enqueue(reminder)
                    }
                    ACTION_RESET_ALL -> {
                        val settings = settingsRepository.getSettingsOnce()
                        val now = System.currentTimeMillis()
                        val monthLabel = buildMonthLabel()
                        meterRepository.resetAllMeters(monthLabel, now)
                        notifier.cancelSwitchToday()
                    }
                }
            } finally {
                result.finish()
            }
        }
    }

    private fun buildMonthLabel(): String {
        val today = LocalDate.now()
        val ym = YearMonth.from(today)
        val month = ym.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        return "$month ${ym.year}"
    }

    companion object {
        const val ACTION_SWITCH_YES = "com.orwyx.unitcalculator.ACTION_SWITCH_YES"
        const val ACTION_SWITCH_NO = "com.orwyx.unitcalculator.ACTION_SWITCH_NO"
        const val ACTION_RESET_ALL = "com.orwyx.unitcalculator.ACTION_RESET_ALL"
        const val EXTRA_METER_ID = "meter_id"
    }
}
