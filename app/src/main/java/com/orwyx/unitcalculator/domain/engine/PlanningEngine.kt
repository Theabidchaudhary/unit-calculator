package com.orwyx.unitcalculator.domain.engine

import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.model.DayPlan
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterPhase
import com.orwyx.unitcalculator.domain.model.MeterWindow
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class PlanningEngine(
    private val strategy: PlanningStrategy = ProportionalStrategy,
) {

    fun buildCalendar(
        totalTarget: Double,
        totalConsumed: Double,
        cycle: BillingCycle,
        meters: List<Meter> = emptyList(),
    ): List<DayPlan> {
        val totalDays = cycle.totalDays
        val avgDaily = if (cycle.elapsedDays > 0) totalConsumed / cycle.elapsedDays else 0.0
        val windows = computeMeterWindows(meters, cycle)

        return (1..totalDays).map { dayIndex ->
            val date = cycle.start.plusDays((dayIndex - 1).toLong())
            val expected = strategy.expectedCumulative(dayIndex, totalDays, totalTarget)
            val elapsedToday = cycle.elapsedDays
            val actual = when {
                dayIndex < elapsedToday -> avgDaily * dayIndex
                dayIndex == elapsedToday -> totalConsumed
                else -> avgDaily * dayIndex
            }
            val window = windows.firstOrNull { it.containsDay(dayIndex) }
            val expectedMeterReading = if (window != null && window.dayCount > 0) {
                val dayInWindow = dayIndex - window.startDay + 1
                val perDay = window.meter.targetLimit / window.dayCount
                window.meter.previousReading + dayInWindow * perDay
            } else 0.0
            DayPlan(
                date = date, dayIndex = dayIndex,
                expectedCumulative = expected, actualCumulative = actual,
                target = totalTarget, isPast = dayIndex < elapsedToday,
                isToday = dayIndex == elapsedToday,
                meterId = window?.meter?.id,
                meterRefLast4 = window?.meter?.referenceLastFour,
                expectedMeterReading = expectedMeterReading,
            )
        }
    }

    fun computeMeterWindows(meters: List<Meter>, cycle: BillingCycle): List<MeterWindow> {
        if (meters.isEmpty()) return emptyList()
        val totalDays = cycle.totalDays
        val windows = mutableListOf<MeterWindow>()
        var currentStart = 1
        for ((index, meter) in meters.withIndex()) {
            if (currentStart > totalDays) break
            val remainingMeters = meters.size - index
            val remainingDays = totalDays - currentStart + 1
            val plannedDays = remainingDays / remainingMeters
            val plannedEnd = currentStart + plannedDays - 1
            val actualEnd = meter.closedDate?.let { closed ->
                val closedDay = (ChronoUnit.DAYS.between(cycle.start, closed).toInt() + 1).coerceIn(1, totalDays)
                if (closedDay >= currentStart) closedDay else plannedEnd
            } ?: plannedEnd
            windows.add(MeterWindow(meter = meter, startDay = currentStart, endDay = actualEnd))
            currentStart = actualEnd + 1
        }
        return windows
    }

    /**
     * Divides the billing cycle into sequential phases proportional to each meter's target share.
     * The first non-locked meter (closedDate == null) is the active phase.
     */
    fun computePhases(meters: List<Meter>, cycle: BillingCycle): List<MeterPhase> {
        if (meters.isEmpty()) return emptyList()
        val totalTarget = meters.sumOf { it.targetLimit }
        if (totalTarget <= 0) return emptyList()

        val totalDays = cycle.totalDays
        val activeIndex = meters.indexOfFirst { !it.isLocked }.takeIf { it >= 0 }
            ?: meters.lastIndex

        var startDay = 1
        return meters.mapIndexed { index, meter ->
            val proportion = meter.targetLimit / totalTarget
            val allocatedDays = if (index == meters.lastIndex) {
                totalDays - startDay + 1
            } else {
                (proportion * totalDays).roundToInt().coerceAtLeast(1)
            }
            val endDay = (startDay + allocatedDays - 1).coerceAtMost(totalDays)
            val phase = MeterPhase(
                meter = meter,
                sequenceIndex = index,
                startDay = startDay,
                endDay = endDay,
                isActive = index == activeIndex,
                elapsedDays = cycle.elapsedDays,
            )
            startDay = endDay + 1
            phase
        }
    }
}

fun interface PlanningStrategy {
    fun expectedCumulative(dayIndex: Int, totalDays: Int, totalTarget: Double): Double
}

val ProportionalStrategy = PlanningStrategy { dayIndex, totalDays, totalTarget ->
    if (totalDays <= 0) 0.0 else totalTarget * (dayIndex.toDouble() / totalDays.toDouble())
}
