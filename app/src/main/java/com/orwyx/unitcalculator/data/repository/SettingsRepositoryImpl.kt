package com.orwyx.unitcalculator.data.repository

import com.orwyx.unitcalculator.data.prefs.SettingsDataStore
import com.orwyx.unitcalculator.domain.model.AppSettings
import com.orwyx.unitcalculator.domain.model.ThemeMode
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore,
) : SettingsRepository {
    override fun observeSettings(): Flow<AppSettings> = dataStore.settings
    override suspend fun getSettingsOnce(): AppSettings = dataStore.settings.first()
    override suspend fun setThemeMode(mode: ThemeMode) = dataStore.setTheme(mode)
    override suspend fun setReadingDate(day: Int) = dataStore.setReadingDate(day)
    override suspend fun setDefaultTarget(target: Double) = dataStore.setDefaultTarget(target)
    override suspend fun setAllowDecimals(allow: Boolean) = dataStore.setAllowDecimals(allow)
    override suspend fun setActiveMeterId(id: Long?) = dataStore.setActiveMeterId(id)
}
