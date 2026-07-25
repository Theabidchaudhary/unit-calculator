package com.orwyx.unitcalculator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.orwyx.unitcalculator.data.local.dao.HistoryDao
import com.orwyx.unitcalculator.data.local.dao.MeterDao
import com.orwyx.unitcalculator.data.local.entity.HistoryEntity
import com.orwyx.unitcalculator.data.local.entity.MeterEntity

@Database(
    entities = [MeterEntity::class, HistoryEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meterDao(): MeterDao
    abstract fun historyDao(): HistoryDao

    companion object {
        const val NAME = "unit_calculator.db"
    }
}
