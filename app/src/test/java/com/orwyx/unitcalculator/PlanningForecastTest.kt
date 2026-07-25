package com.orwyx.unitcalculator

import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.engine.ForecastEngine
import com.orwyx.unitcalculator.domain.engine.PlanningEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PlanningForecastTest {
    private val forecastEngine = ForecastEngine()
    private val planningEngine = PlanningEngine()
    private val cycle = BillingCycle(start = LocalDate.of(2026, 7, 1), end = LocalDate.of(2026, 7, 31), today = LocalDate.of(2026, 7, 11))

    @Test fun `cycle total days is inclusive`() { assertEquals(31, cycle.totalDays) }

    @Test fun `forecast projects and flags overage`() {
        val f = forecastEngine.forecast(totalConsumed = 120.0, totalTarget = 200.0, cycle = cycle)
        assertEquals(10.909, f.avgDailyUsage, 0.01)
        assertEquals(338.18, f.projectedMonthEnd, 0.1)
        assertEquals(138.18, f.expectedOverage, 0.1)
        assertTrue(f.willExceed)
    }

    @Test fun `forecast under target does not flag`() {
        val f = forecastEngine.forecast(totalConsumed = 40.0, totalTarget = 200.0, cycle = cycle)
        assertFalse(f.willExceed)
        assertTrue(f.expectedOverage < 0)
    }

    @Test fun `calendar spans the full cycle`() {
        val days = planningEngine.buildCalendar(300.0, 60.0, cycle)
        assertEquals(31, days.size)
        assertEquals(1, days.first().dayIndex)
        assertEquals(31, days.last().dayIndex)
    }

    @Test fun `expected cumulative follows proportional pace`() {
        val days = planningEngine.buildCalendar(300.0, 60.0, cycle)
        assertEquals(154.84, days[15].expectedCumulative, 0.1)
        assertEquals(300.0, days.last().expectedCumulative, 0.001)
    }

    @Test fun `today uses real consumed and is flagged`() {
        val days = planningEngine.buildCalendar(300.0, 60.0, cycle)
        val today = days.first { it.isToday }
        assertEquals(11, today.dayIndex)
        assertEquals(60.0, today.actualCumulative, 0.001)
    }

    @Test fun `future days are projected at running pace`() {
        val days = planningEngine.buildCalendar(300.0, 60.0, cycle)
        val future = days.first { it.dayIndex == 20 }
        assertTrue(future.isFuture)
        assertEquals(109.09, future.actualCumulative, 0.1)
    }
}
