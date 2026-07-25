package com.orwyx.unitcalculator.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import com.orwyx.unitcalculator.data.local.AppDatabase
import com.orwyx.unitcalculator.data.local.dao.HistoryDao
import com.orwyx.unitcalculator.data.local.dao.MeterDao
import com.orwyx.unitcalculator.domain.model.ElectricityProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .addCallback(SeedCallback)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMeterDao(db: AppDatabase): MeterDao = db.meterDao()

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()

    private object SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            val now = System.currentTimeMillis()
            db.execSQL("""
                INSERT INTO meters
                (name, referenceNumber, providerId, targetLimit, previousReading,
                 currentReading, createdAt, updatedAt, sortOrder, closedDateEpochDay)
                VALUES ('House', '00000000000', '${ElectricityProvider.DEFAULT.id}',
                        200.0, 0.0, 0.0, $now, $now, 0, 0)
            """.trimIndent())
        }
    }
}
