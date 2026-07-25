package com.orwyx.unitcalculator.domain.engine

import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.model.DashboardSummary
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterStatus

/**
 * Pure, framework-free consumption maths. Every figure the UI shows for a meter or the dashboard
 * is derived here, so nothing has to be entered or recomputed by hand. Fully unit-testable.
 */
class CalculationEngine {

    /** Average units per day for a single meter over the elapsed portion of the cycle. */
    fun averageDailyUsage(meter: Meter, cycle: BillingCycle): Double =
        meter.consumedUnits / cycle.elapsedDays

    /** Straight-line projection of month-end consumption if the current pace continues. */
    fun projectedMonthEnd(meter: Meter, cycle: BillingCycle): Double =
        averageDailyUsage(meter, cycle) * cycle.totalDays

    /** Units projected to exceed the target at month end. Negative means projected to stay under. */
    fun projectedOverage(meter: Meter, cycle: BillingCycle): Double =
        projectedMonthEnd(meter, cycle) - meter.targetLimit

    /** Aggregate every meter into the dashboard summary for a given billing cycle. */
    fun summarize(meters: List<Meter>, cycle: BillingCycle): DashboardSummary {
        if (meters.isEmpty()) return DashboardSummary()

        val totalConsumed = meters.sumOf { it.consumedUnits }
        val totalTarget = meters.sumOf { it.targetLimit }
        val totalRemaining = meters.sumOf { it.remainingUnits }
        val avgDaily = meters.sumOf { averageDailyUsage(it, cycle) }
        val projected = meters.sumOf { projectedMonthEnd(it, cycle) }

        var safe = 0
        var warning = 0
        var critical = 0
        meters.forEach {
            when (it.status) {
                MeterStatus.SAFE, MeterStatus.MODERATE -> safe++
                MeterStatus.WARNING -> warning++
                MeterStatus.CRITICAL, MeterStatus.EXCEEDED -> critical++
            }
        }

        return DashboardSummary(
            totalMeters = meters.size,
            totalConsumed = totalConsumed,
            totalRemaining = totalRemaining,
            totalTarget = totalTarget,
            avgDailyUsage = avgDaily,
            projectedMonthEnd = projected,
            safeCount = safe,
            warningCount = warning,
            criticalCount = critical,
        )
    }
}
