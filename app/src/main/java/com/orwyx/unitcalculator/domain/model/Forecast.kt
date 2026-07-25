package com.orwyx.unitcalculator.domain.model

/** Cycle-level forecast summary shown on the planning screen. */
data class Forecast(
    val avgDailyUsage: Double = 0.0,
    val projectedMonthEnd: Double = 0.0,
    val target: Double = 0.0,
    val expectedOverage: Double = 0.0, // negative means projected to stay under
) {
    val willExceed: Boolean get() = expectedOverage > 0.0

    val projectedFraction: Float
        get() = if (target <= 0.0) 0f else (projectedMonthEnd / target).toFloat()

    val safety: MeterStatus get() = MeterStatus.fromPercent(projectedFraction)
}
