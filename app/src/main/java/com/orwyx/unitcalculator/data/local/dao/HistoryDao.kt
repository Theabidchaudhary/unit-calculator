package com.orwyx.unitcalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orwyx.unitcalculator.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM reading_history ORDER BY closedAt DESC")
    fun observeAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM reading_history WHERE meterId = :meterId ORDER BY closedAt DESC")
    fun observeForMeter(meterId: Long): Flow<List<HistoryEntity>>

    @Insert
    suspend fun insert(history: HistoryEntity): Long

    @Query("SELECT * FROM reading_history")
    suspend fun getAllOnce(): List<HistoryEntity>

    @Insert
    suspend fun insertAll(history: List<HistoryEntity>)

    @Query("DELETE FROM reading_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM reading_history")
    suspend fun clearAll()
}
