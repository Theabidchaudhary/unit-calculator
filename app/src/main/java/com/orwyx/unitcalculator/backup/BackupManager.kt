package com.orwyx.unitcalculator.backup

import androidx.room.withTransaction
import com.orwyx.unitcalculator.data.local.AppDatabase
import com.orwyx.unitcalculator.data.local.entity.HistoryEntity
import com.orwyx.unitcalculator.data.local.entity.MeterEntity
import com.orwyx.unitcalculator.data.prefs.SettingsDataStore
import com.orwyx.unitcalculator.domain.model.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

sealed interface BackupResult {
    data class Success(val meterCount: Int, val historyCount: Int) : BackupResult
    data class Error(val message: String) : BackupResult
}

/**
 * Serializes and restores the full local dataset (meters, history, settings) to/from JSON.
 * Isolated from UI: it works on strings so the caller owns file access via the Storage Access
 * Framework. Restores run in a single transaction so a bad import can't leave partial data.
 */
@Singleton
class BackupManager @Inject constructor(
    private val db: AppDatabase,
    private val settings: SettingsDataStore,
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** Produces a JSON snapshot string of everything stored locally. */
    suspend fun export(): String = withContext(Dispatchers.IO) {
        val meters = db.meterDao().getAllOnce()
        val history = db.historyDao().getAllOnce()
        val s = settings.settings.first()
        val backup = BackupFile(
            exportedAt = System.currentTimeMillis(),
            settings = SettingsBackup(s.themeMode.name, s.readingDate, s.defaultTarget, s.allowDecimals, s.activeMeterId),
            meters = meters.map { it.toBackup() },
            history = history.map { it.toBackup() },
        )
        json.encodeToString(backup)
    }

    /** Replaces all local data with the contents of [content]. Returns a typed result. */
    suspend fun import(content: String): BackupResult = withContext(Dispatchers.IO) {
        val backup = runCatching { json.decodeFromString<BackupFile>(content) }
            .getOrElse { return@withContext BackupResult.Error("This file isn't a valid Unit Calculator backup.") }

        if (backup.version > BackupFile.CURRENT_VERSION) {
            return@withContext BackupResult.Error("This backup was made by a newer app version.")
        }

        runCatching {
            db.withTransaction {
                db.historyDao().clearAll()
                db.meterDao().deleteAll()
                db.meterDao().insertAll(backup.meters.map { it.toEntity() })
                db.historyDao().insertAll(backup.history.map { it.toEntity() })
            }
            restoreSettings(backup.settings)
        }.fold(
            onSuccess = { BackupResult.Success(backup.meters.size, backup.history.size) },
            onFailure = { BackupResult.Error("Could not restore this backup: ${it.message}") },
        )
    }

    private suspend fun restoreSettings(s: SettingsBackup) {
        settings.setTheme(runCatching { ThemeMode.valueOf(s.themeMode) }.getOrDefault(ThemeMode.SYSTEM))
        settings.setReadingDate(s.readingDate)
        settings.setDefaultTarget(s.defaultTarget)
        settings.setAllowDecimals(s.allowDecimals)
        settings.setActiveMeterId(s.activeMeterId)
    }
}

private fun MeterEntity.toBackup() = MeterBackup(
    id, name, referenceNumber, providerId, targetLimit, previousReading,
    currentReading, createdAt, updatedAt, sortOrder, closedDateEpochDay,
)

private fun MeterBackup.toEntity() = MeterEntity(
    id, name, referenceNumber, providerId, targetLimit, previousReading,
    currentReading, createdAt, updatedAt, sortOrder, closedDateEpochDay,
)

private fun HistoryEntity.toBackup() = HistoryBackup(
    id, meterId, monthLabel, previousReading, currentReading, unitsConsumed,
    target, remaining, billAmount, avgDailyUsage, status, closedAt,
)

private fun HistoryBackup.toEntity() = HistoryEntity(
    id, meterId, monthLabel, previousReading, currentReading, unitsConsumed,
    target, remaining, billAmount, avgDailyUsage, status, closedAt,
)
