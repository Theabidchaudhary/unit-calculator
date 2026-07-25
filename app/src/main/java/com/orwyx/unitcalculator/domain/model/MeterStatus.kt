package com.orwyx.unitcalculator.domain.model

/**
 * Consumption status of a meter, derived purely from the percentage of the target consumed.
 * Ordering matters: [ordinal] is used to aggregate the "most dangerous" status across meters.
 */
enum class MeterStatus(val label: String, val emoji: String) {
    SAFE("Safe", "🟢"),
    MODERATE("Moderate", "🟡"),
    WARNING("Warning", "🟠"),
    CRITICAL("Critical", "🔴"),
    EXCEEDED("Limit Exceeded", "⚫");

    companion object {
        /**
         * Maps a used-percentage (0f..1f+, where 1f == exactly at target) to a status band.
         * Bands: <50 Safe, <75 Moderate, <90 Warning, <100 Critical, >=100 Exceeded.
         */
        fun fromPercent(percent: Float): MeterStatus = when {
            percent >= 1.0f -> EXCEEDED
            percent >= 0.90f -> CRITICAL
            percent >= 0.75f -> WARNING
            percent >= 0.50f -> MODERATE
            else -> SAFE
        }
    }
}
