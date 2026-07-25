package com.orwyx.unitcalculator.domain.engine

import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.model.Forecast

/**
 * Turns "consumed so far" into a month-end forecast. Kept separate from [CalculationEngine] so the
 * forecasting strategy (straight-line today; ML later) can evolve independently. Pure & testable.
 */
class ForecastEngine {

    fun forecast(totalConsumed: Double, totalTarget: Double, cycle: BillingCycle): Forecast {
        val avgDaily = totalConsumed / cycle.elapsedDays
        val projected = avgDaily * cycle.totalDays
        return Forecast(
            avgDailyUsage = avgDaily,
            projectedMonthEnd = projected,
            target = totalTarget,
            expectedOverage = projected - totalTarget,
        )
    }
}
