package com.orwyx.unitcalculator.domain.repository

import com.orwyx.unitcalculator.domain.model.ReadingHistory
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeAll(): Flow<List<ReadingHistory>>
    fun observeForMeter(meterId: Long): Flow<List<ReadingHistory>>
    suspend fun delete(id: Long)
    suspend fun clearAll()
}
