package com.orwyx.unitcalculator.domain.model

/** Aggregated figures across all meters, shown in the dashboard summary cards. */
data class DashboardSummary(
    val totalMeters: Int = 0,
    val totalConsumed: Double = 0.0,
    val totalRemaining: Double = 0.0,
    val totalTarget: Double = 0.0,
    val avgDailyUsage: Double = 0.0,
    val projectedMonthEnd: Double = 0.0,
    val safeCount: Int = 0,
    val warningCount: Int = 0,
    val criticalCount: Int = 0,
) {
    val overallFraction: Float
        get() = if (totalTarget <= 0.0) 0f else (totalConsumed / totalTarget).toFloat()
}
