package com.orwyx.unitcalculator.core.util

import java.time.LocalDate
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
        fun of(readingDate: Int, today: LocalDate = LocalDate.now()): BillingCycle {
            val day = readingDate.coerceIn(1, 31)
            val startThisMonth = today.withDayOfMonth(day)
            val start = if (today.dayOfMonth >= day) startThisMonth else startThisMonth.minusMonths(1)
            val end = start.plusMonths(1)
            return BillingCycle(start = start, end = end, today = today)
        }
    }
}
