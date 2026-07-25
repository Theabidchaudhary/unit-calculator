package com.orwyx.unitcalculator.domain.repository

import com.orwyx.unitcalculator.domain.model.AppSettings
import com.orwyx.unitcalculator.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun getSettingsOnce(): AppSettings
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setReadingDate(day: Int)
    suspend fun setDefaultTarget(target: Double)
    suspend fun setAllowDecimals(allow: Boolean)
    suspend fun setActiveMeterId(id: Long?)
}
