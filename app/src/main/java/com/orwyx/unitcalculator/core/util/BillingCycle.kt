package com.orwyx.unitcalculator.core.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class BillingCycle(
    val start: LocalDate,
    val end: LocalDate,
    val today: LocalDate,
) {
    val totalDays: Int get() = (ChronoUnit.DAYS.between(start, end).toInt() + 1).coerceAtLeast(1)
    val elapsedDays: Int get() = (ChronoUnit.DAYS.between(start, today).toInt() + 1).coerceIn(1, totalDays)
    val remainingDays: Int get() = (totalDays - elapsedDays).coerceAtLeast(0)
    val progressFraction: Float get() = elapsedDays.toFloat() / totalDays.toFloat()

    companion object {
        /**
         * Half-day rule: on the reading date itself, the first 12 hours (00:00–11:59) still
         * count toward the old cycle. At 12:00 noon the app switches to the new cycle.
         * Before noon on reading date → old cycle; at/after noon → new cycle.
         */
        fun of(
            readingDate: Int,
            today: LocalDate = LocalDate.now(),
            now: LocalTime = LocalTime.now(),
        ): BillingCycle {
            val day = readingDate.coerceIn(1, 28) // safe across all months
            val startThisMonth = safeWithDayOfMonth(today, day)
            val isReadingDay = today.dayOfMonth == day
            // Switch to new cycle if strictly past reading date, or exactly on reading date at/after noon
            val inNewCycle = today.dayOfMonth > day || (isReadingDay && now.hour >= 12)
            val start = if (inNewCycle) startThisMonth else startThisMonth.minusMonths(1)
            val end = safeWithDayOfMonth(start.plusMonths(1), day)
            return BillingCycle(start = start, end = end, today = today)
        }

        /** True if the reading date has passed this month and the user should reset. */
        fun isExpired(readingDate: Int, lastResetEpochDay: Long, today: LocalDate = LocalDate.now()): Boolean {
            val day = readingDate.coerceIn(1, 28)
            if (today.dayOfMonth <= day) return false
            val thisMonthReadingDate = safeWithDayOfMonth(today, day)
            return lastResetEpochDay < thisMonthReadingDate.toEpochDay()
        }

        private fun safeWithDayOfMonth(date: LocalDate, day: Int): LocalDate {
            val maxDay = date.lengthOfMonth()
            return date.withDayOfMonth(day.coerceAtMost(maxDay))
        }
    }
}
