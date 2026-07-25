package com.orwyx.unitcalculator

import com.orwyx.unitcalculator.core.util.BillingCycle
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class BillingCycleTest {
    @Test fun `cycle starts on reading date this month when today is after it`() {
        val cycle = BillingCycle.of(readingDate = 7, today = LocalDate.of(2026, 7, 20))
        assertEquals(LocalDate.of(2026, 7, 7), cycle.start)
        assertEquals(LocalDate.of(2026, 8, 7), cycle.end)
    }
    @Test fun `cycle starts previous month when today is before reading date`() {
        val cycle = BillingCycle.of(readingDate = 25, today = LocalDate.of(2026, 7, 10))
        assertEquals(LocalDate.of(2026, 6, 25), cycle.start)
        assertEquals(LocalDate.of(2026, 7, 25), cycle.end)
    }
    @Test fun `elapsed days clamped to at least one`() {
        val cycle = BillingCycle.of(readingDate = 7, today = LocalDate.of(2026, 7, 7))
        assertEquals(1, cycle.elapsedDays)
    }
    @Test fun `reading date is clamped to 31`() {
        val cycle = BillingCycle.of(readingDate = 35, today = LocalDate.of(2026, 7, 15))
        assertEquals(31, cycle.start.dayOfMonth)
    }
    @Test fun `total days is inclusive of both start and end`() {
        val cycle = BillingCycle.of(readingDate = 7, today = LocalDate.of(2026, 7, 20))
        assertEquals(32, cycle.totalDays)
    }
}
