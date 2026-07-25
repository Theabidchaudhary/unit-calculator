package com.orwyx.unitcalculator.data.repository

import com.orwyx.unitcalculator.data.local.dao.HistoryDao
import com.orwyx.unitcalculator.data.mapper.toDomain
import com.orwyx.unitcalculator.domain.model.ReadingHistory
import com.orwyx.unitcalculator.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao,
) : HistoryRepository {
    override fun observeAll(): Flow<List<ReadingHistory>> =
        historyDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeForMeter(meterId: Long): Flow<List<ReadingHistory>> =
        historyDao.observeForMeter(meterId).map { list -> list.map { it.toDomain() } }

    override suspend fun delete(id: Long) = historyDao.deleteById(id)
    override suspend fun clearAll() = historyDao.clearAll()
}
