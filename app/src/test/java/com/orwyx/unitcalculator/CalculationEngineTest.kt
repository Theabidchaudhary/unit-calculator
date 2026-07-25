package com.orwyx.unitcalculator

import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.engine.CalculationEngine
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CalculationEngineTest {

    private val engine = CalculationEngine()

    private fun meter(prev: Double, curr: Double, target: Double = 200.0) = Meter(
        id = 1,
        name = "Test",
        referenceNumber = "12345678",
        targetLimit = target,
        previousReading = prev,
        currentReading = curr,
    )

    @Test
    fun `consumed is current minus previous`() {
        assertEquals(120.0, meter(1000.0, 1120.0).consumedUnits, 0.001)
    }

    @Test
    fun `consumed never goes negative`() {
        assertEquals(0.0, meter(1120.0, 1000.0).consumedUnits, 0.001)
    }

    @Test
    fun `remaining is target minus consumed`() {
        assertEquals(80.0, meter(1000.0, 1120.0, target = 200.0).remainingUnits, 0.001)
    }

    @Test
    fun `used fraction and status bands`() {
        assertEquals(MeterStatus.SAFE, meter(0.0, 40.0).status)      // 20%
        assertEquals(MeterStatus.MODERATE, meter(0.0, 120.0).status) // 60%
        assertEquals(MeterStatus.WARNING, meter(0.0, 160.0).status)  // 80%
        assertEquals(MeterStatus.CRITICAL, meter(0.0, 190.0).status) // 95%
        assertEquals(MeterStatus.EXCEEDED, meter(0.0, 220.0).status) // 110%
    }

    @Test
    fun `zero target is treated as no consumption fraction`() {
        assertEquals(0f, meter(0.0, 50.0, target = 0.0).usedFraction, 0.001f)
    }

    @Test
    fun `average daily usage divides consumed by elapsed days`() {
        val cycle = BillingCycle(
            start = LocalDate.of(2026, 7, 1),
            end = LocalDate.of(2026, 8, 1),
            today = LocalDate.of(2026, 7, 11),
        )
        // 100 units over 10 elapsed days = 10/day
        assertEquals(10.0, engine.averageDailyUsage(meter(0.0, 100.0), cycle), 0.001)
    }

    @Test
    fun `projection extrapolates to full cycle`() {
        val cycle = BillingCycle(
            start = LocalDate.of(2026, 7, 1),
            end = LocalDate.of(2026, 7, 31),
            today = LocalDate.of(2026, 7, 11),
        )
        // 10/day over 30 total days = 300 projected
        assertEquals(300.0, engine.projectedMonthEnd(meter(0.0, 100.0), cycle), 0.001)
    }

    @Test
    fun `summary aggregates and counts statuses`() {
        val cycle = BillingCycle.of(1, LocalDate.of(2026, 7, 15))
        val meters = listOf(
            meter(0.0, 40.0),   // safe
            meter(0.0, 160.0),  // warning
            meter(0.0, 220.0),  // exceeded -> critical bucket
        )
        val summary = engine.summarize(meters, cycle)
        assertEquals(3, summary.totalMeters)
        assertEquals(420.0, summary.totalConsumed, 0.001)
        assertEquals(1, summary.safeCount)
        assertEquals(1, summary.warningCount)
        assertEquals(1, summary.criticalCount)
        assertTrue(summary.totalTarget > 0)
    }
}
