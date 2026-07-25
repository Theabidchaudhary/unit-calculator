package com.orwyx.unitcalculator.domain.model

data class MeterWindow(
    val meter: Meter,
    val startDay: Int,
    val endDay: Int,
) {
    val dayCount: Int get() = (endDay - startDay + 1).coerceAtLeast(0)
    fun containsDay(dayIndex: Int): Boolean = dayIndex in startDay..endDay
}
