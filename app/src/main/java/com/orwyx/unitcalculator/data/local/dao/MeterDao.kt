package com.orwyx.unitcalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.orwyx.unitcalculator.data.local.entity.MeterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeterDao {

    @Query("SELECT * FROM meters ORDER BY sortOrder ASC, createdAt ASC")
    fun observeAll(): Flow<List<MeterEntity>>

    @Query("SELECT * FROM meters WHERE id = :id")
    fun observeById(id: Long): Flow<MeterEntity?>

    @Query("SELECT * FROM meters WHERE id = :id")
    suspend fun getById(id: Long): MeterEntity?

    @Query("SELECT COUNT(*) FROM meters")
    suspend fun count(): Int

    @Query("SELECT * FROM meters ORDER BY sortOrder ASC, createdAt ASC")
    suspend fun getAllOnce(): List<MeterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meters: List<MeterEntity>)

    @Query("DELETE FROM meters")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meter: MeterEntity): Long

    @Update
    suspend fun update(meter: MeterEntity)

    @Query("DELETE FROM meters WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE meters SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int)

    @Query("UPDATE meters SET closedDateEpochDay = :epochDay WHERE id = :id")
    suspend fun updateClosedDate(id: Long, epochDay: Long)

    @Query("UPDATE meters SET closedDateEpochDay = 0")
    suspend fun clearAllClosedDates()
}
