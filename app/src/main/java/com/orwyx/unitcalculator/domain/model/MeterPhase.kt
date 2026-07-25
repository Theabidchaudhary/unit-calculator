package com.orwyx.unitcalculator.domain.model

/**
 * Represents the slice of the billing cycle allocated to one meter in the sequential plan.
 * [startDay]/[endDay] are 1-indexed positions within the billing cycle (1..totalDays).
 * [isActive] is derived from the lock state: the first non-locked meter in sequence is active.
 */
data class MeterPhase(
    val meter: Meter,
    val sequenceIndex: Int,
    val startDay: Int,
    val endDay: Int,
    val isActive: Boolean,
    val elapsedDays: Int,
) {
    val allocatedDays: Int get() = endDay - startDay + 1
    val isComplete: Boolean get() = meter.isLocked
    val isPending: Boolean get() = !meter.isLocked && !isActive

    private val daysConsuming: Int get() = when {
        isComplete -> allocatedDays
        isActive -> (elapsedDays - startDay + 1).coerceIn(1, allocatedDays)
        else -> 0
    }

    val remainingDaysInPhase: Int get() = when {
        isComplete || isPending -> 0
        else -> (endDay - elapsedDays).coerceAtLeast(0)
    }

    val avgDailyUsage: Double get() = when {
        daysConsuming <= 0 -> 0.0
        else -> meter.consumedUnits / daysConsuming
    }

    val daysUntilExhaustion: Double get() = when {
        meter.remainingUnits <= 0 -> 0.0
        avgDailyUsage <= 0.0 -> Double.MAX_VALUE
        else -> meter.remainingUnits / avgDailyUsage
    }
}
