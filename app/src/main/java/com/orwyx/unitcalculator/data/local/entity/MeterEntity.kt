package com.orwyx.unitcalculator.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meters")
data class MeterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val referenceNumber: String,
    val providerId: String,
    val targetLimit: Double,
    val previousReading: Double,
    val currentReading: Double,
    @ColumnInfo(defaultValue = "0") val createdAt: Long,
    @ColumnInfo(defaultValue = "0") val updatedAt: Long,
    @ColumnInfo(defaultValue = "0") val sortOrder: Int = 0,
    @ColumnInfo(defaultValue = "0") val closedDateEpochDay: Long = 0L,
)
