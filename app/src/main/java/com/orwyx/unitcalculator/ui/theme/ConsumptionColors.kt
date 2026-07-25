package com.orwyx.unitcalculator.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.orwyx.unitcalculator.domain.model.MeterStatus

/**
 * Maps a consumption fraction (0f..1f+) to a colour along a smooth seven-stop gradient
 * from deep green through yellow and orange to dark red. This is the single source of truth
 * for every progress bar, badge and calendar cell so the visual language stays consistent.
 */
object ConsumptionColors {

    private val stops = listOf(
        0.0f to StatusDeepGreen,
        0.35f to StatusGreen,
        0.55f to StatusYellowGreen,
        0.70f to StatusYellow,
        0.85f to StatusOrange,
        1.0f to StatusRed,
        1.15f to StatusDarkRed,
    )

    /** Smoothly interpolated colour for the given fraction, clamped to the gradient range. */
    fun colorFor(fraction: Float): Color {
        val f = fraction.coerceIn(0f, stops.last().first)
        for (i in 0 until stops.size - 1) {
            val (lowF, lowC) = stops[i]
            val (highF, highC) = stops[i + 1]
            if (f in lowF..highF) {
                val t = if (highF == lowF) 0f else (f - lowF) / (highF - lowF)
                return lerp(lowC, highC, t)
            }
        }
        return stops.last().second
    }

    /** Discrete colour for a status badge. */
    fun colorFor(status: MeterStatus): Color = when (status) {
        MeterStatus.SAFE -> StatusDeepGreen
        MeterStatus.MODERATE -> StatusYellowGreen
        MeterStatus.WARNING -> StatusOrange
        MeterStatus.CRITICAL -> StatusRed
        MeterStatus.EXCEEDED -> StatusDarkRed
    }
}
