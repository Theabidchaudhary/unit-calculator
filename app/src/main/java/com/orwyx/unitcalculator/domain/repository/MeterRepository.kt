package com.orwyx.unitcalculator.domain.repository

import com.orwyx.unitcalculator.domain.model.Meter
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MeterRepository {
    fun observeMeters(): Flow<List<Meter>>
    fun observeMeter(id: Long): Flow<Meter?>
    suspend fun getMeter(id: Long): Meter?
    suspend fun getAllMeters(): List<Meter>
    suspend fun upsert(meter: Meter): Long
    suspend fun delete(id: Long)
    suspend fun count(): Int
    suspend fun resetMonth(id: Long, monthLabel: String, avgDailyUsage: Double, closedAt: Long)
    suspend fun setClosedDate(id: Long, closedDate: LocalDate?)
    suspend fun reorderMeters(orderedIds: List<Long>)
    suspend fun lockMeter(id: Long)
    suspend fun unlockAllMeters()
    suspend fun resetAllMeters(monthLabel: String, closedAt: Long)
}
