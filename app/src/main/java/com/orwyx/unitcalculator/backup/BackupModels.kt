package com.orwyx.unitcalculator.backup

import kotlinx.serialization.Serializable

@Serializable
data class BackupFile(
    val version: Int = CURRENT_VERSION,
    val exportedAt: Long,
    val settings: SettingsBackup,
    val meters: List<MeterBackup>,
    val history: List<HistoryBackup>,
) {
    companion object { const val CURRENT_VERSION = 1 }
}

@Serializable
data class SettingsBackup(
    val themeMode: String, val readingDate: Int,
    val defaultTarget: Double, val allowDecimals: Boolean,
    val activeMeterId: Long? = null,
)

@Serializable
data class MeterBackup(
    val id: Long, val name: String, val referenceNumber: String,
    val providerId: String, val targetLimit: Double,
    val previousReading: Double, val currentReading: Double,
    val createdAt: Long, val updatedAt: Long, val sortOrder: Int,
    val closedDateEpochDay: Long = 0L,
)

@Serializable
data class HistoryBackup(
    val id: Long, val meterId: Long, val monthLabel: String,
    val previousReading: Double, val currentReading: Double,
    val unitsConsumed: Double, val target: Double, val remaining: Double,
    val billAmount: Double?, val avgDailyUsage: Double,
    val status: String, val closedAt: Long,
)
