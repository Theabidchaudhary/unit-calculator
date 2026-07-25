package com.orwyx.unitcalculator.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading_history",
    foreignKeys = [
        ForeignKey(
            entity = MeterEntity::class,
            parentColumns = ["id"],
            childColumns = ["meterId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("meterId")],
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val meterId: Long,
    val monthLabel: String,
    val previousReading: Double,
    val currentReading: Double,
    val unitsConsumed: Double,
    val target: Double,
    val remaining: Double,
    val billAmount: Double?,
    val avgDailyUsage: Double,
    val status: String,
    val closedAt: Long,
)
