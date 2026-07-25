package com.orwyx.unitcalculator.domain.model

/** A completed billing month, snapshotted when a meter is reset. Immutable once created. */
data class ReadingHistory(
    val id: Long = 0L,
    val meterId: Long,
    val monthLabel: String,
    val previousReading: Double,
    val currentReading: Double,
    val unitsConsumed: Double,
    val target: Double,
    val remaining: Double,
    val billAmount: Double? = null,
    val avgDailyUsage: Double,
    val status: MeterStatus,
    val closedAt: Long,
)
