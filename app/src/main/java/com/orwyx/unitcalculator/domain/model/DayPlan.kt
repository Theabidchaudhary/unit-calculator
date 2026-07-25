package com.orwyx.unitcalculator.domain.model

import java.time.LocalDate

data class DayPlan(
    val date: LocalDate,
    val dayIndex: Int,
    val expectedCumulative: Double,
    val actualCumulative: Double,
    val target: Double,
    val isPast: Boolean,
    val isToday: Boolean,
    val meterId: Long? = null,
    val meterRefLast4: String? = null,
    val expectedMeterReading: Double = 0.0,
) {
    val isFuture: Boolean get() = !isPast && !isToday
    val difference: Double get() = actualCumulative - expectedCumulative
    val usedFraction: Float get() = if (target <= 0.0) 0f else (actualCumulative / target).toFloat()
    val status: MeterStatus get() = MeterStatus.fromPercent(usedFraction)
}
