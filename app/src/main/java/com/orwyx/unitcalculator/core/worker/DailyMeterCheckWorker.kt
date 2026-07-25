package com.orwyx.unitcalculator.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orwyx.unitcalculator.core.notification.MeterAlertNotifier
import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.engine.PlanningEngine
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyMeterCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val meterRepository: MeterRepository,
    private val settingsRepository: SettingsRepository,
    private val planningEngine: PlanningEngine,
    private val notifier: MeterAlertNotifier,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val isReminder = inputData.getBoolean(KEY_IS_REMINDER, false)
            val remindMeterId = inputData.getLong(KEY_METER_ID, -1L)

            if (isReminder && remindMeterId > 0) {
                handleSwitchReminder(remindMeterId)
            } else {
                handleDailyCheck()
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun handleDailyCheck() {
        val settings = settingsRepository.getSettingsOnce()
        val cycle = BillingCycle.of(settings.readingDate)
        val meters = meterRepository.getAllMeters()

        if (meters.isEmpty()) return

        // Check if billing cycle just ended
        if (cycle.remainingDays == 0) {
            notifier.sendCycleEndNotification()
            return
        }

        val phases = planningEngine.computePhases(meters, cycle)
        val activePhase = phases.firstOrNull { it.isActive } ?: return
        val nextMeter = phases.getOrNull(activePhase.sequenceIndex + 1)?.meter

        val daysLeft = activePhase.daysUntilExhaustion
        when {
            daysLeft <= 1.0 -> notifier.sendSwitchTodayNotification(
                activeMeterName = activePhase.meter.name,
                activMeterId = activePhase.meter.id,
                nextMeterName = nextMeter?.name,
            )
            daysLeft <= 2.0 -> notifier.sendSwitchTomorrowNotification(
                activeMeterName = activePhase.meter.name,
                nextMeterName = nextMeter?.name,
                remainingUnits = activePhase.meter.remainingUnits,
            )
        }
    }

    private suspend fun handleSwitchReminder(meterId: Long) {
        val meter = meterRepository.getMeter(meterId) ?: return
        if (meter.isLocked) return // User already confirmed switch
        val settings = settingsRepository.getSettingsOnce()
        val cycle = BillingCycle.of(settings.readingDate)
        val meters = meterRepository.getAllMeters()
        val phases = planningEngine.computePhases(meters, cycle)
        val nextMeter = phases.firstOrNull { it.sequenceIndex > (phases.indexOfFirst { p -> p.meter.id == meterId }) }?.meter
        notifier.sendSwitchTodayNotification(
            activeMeterName = meter.name,
            activMeterId = meter.id,
            nextMeterName = nextMeter?.name,
        )
    }

    companion object {
        const val KEY_IS_REMINDER = "is_reminder"
        const val KEY_METER_ID = "meter_id"
        const val TAG_SWITCH_REMINDER = "switch_reminder"
        const val WORK_NAME_DAILY = "daily_meter_check"
    }
}
