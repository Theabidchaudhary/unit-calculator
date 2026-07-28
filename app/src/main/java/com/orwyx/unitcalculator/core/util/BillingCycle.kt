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
            val day = readingDate.coerceIn(1, 31)
            // Effective day for this month (e.g. day 31 in Feb → last day of Feb)
            val startThisMonth = safeWithDayOfMonth(today, day)
            val effectiveDay = startThisMonth.dayOfMonth
            val isReadingDay = today.dayOfMonth == effectiveDay
            // Half-day rule: first 12 hours of reading date belong to old cycle; noon onwards to new cycle
            val inNewCycle = today.dayOfMonth > effectiveDay || (isReadingDay && now.hour >= 12)
            val start = if (inNewCycle) startThisMonth else safeWithDayOfMonth(today.minusMonths(1), day)
            val end = safeWithDayOfMonth(start.plusMonths(1), day)
            return BillingCycle(start = start, end = end, today = today)
        }

        /** True if the reading date has passed this month and the user should reset. */
        fun isExpired(readingDate: Int, lastResetEpochDay: Long, today: LocalDate = LocalDate.now()): Boolean {
            val day = readingDate.coerceIn(1, 31)
            val thisMonthReadingDate = safeWithDayOfMonth(today, day)
            if (today.dayOfMonth <= thisMonthReadingDate.dayOfMonth) return false
            return lastResetEpochDay < thisMonthReadingDate.toEpochDay()
        }

        private fun safeWithDayOfMonth(date: LocalDate, day: Int): LocalDate {
            val maxDay = date.lengthOfMonth()
            return date.withDayOfMonth(day.coerceAtMost(maxDay))
        }
    }
}
