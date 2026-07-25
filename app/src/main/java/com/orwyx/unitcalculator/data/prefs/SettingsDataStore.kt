package com.orwyx.unitcalculator.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orwyx.unitcalculator.domain.model.AppSettings
import com.orwyx.unitcalculator.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val READING_DATE = intPreferencesKey("reading_date")
        val DEFAULT_TARGET = doublePreferencesKey("default_target")
        val ALLOW_DECIMALS = booleanPreferencesKey("allow_decimals")
        val ACTIVE_METER_ID = longPreferencesKey("active_meter_id")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            readingDate = prefs[Keys.READING_DATE] ?: 1,
            defaultTarget = prefs[Keys.DEFAULT_TARGET] ?: 200.0,
            allowDecimals = prefs[Keys.ALLOW_DECIMALS] ?: false,
            activeMeterId = prefs[Keys.ACTIVE_METER_ID]?.takeIf { it > 0L },
        )
    }

    suspend fun setTheme(mode: ThemeMode) = context.dataStore.edit { it[Keys.THEME] = mode.name }.let {}
    suspend fun setReadingDate(day: Int) = context.dataStore.edit { it[Keys.READING_DATE] = day.coerceIn(1, 31) }.let {}
    suspend fun setDefaultTarget(target: Double) = context.dataStore.edit { it[Keys.DEFAULT_TARGET] = target }.let {}
    suspend fun setAllowDecimals(allow: Boolean) = context.dataStore.edit { it[Keys.ALLOW_DECIMALS] = allow }.let {}
    suspend fun setActiveMeterId(id: Long?) = context.dataStore.edit { it[Keys.ACTIVE_METER_ID] = (id ?: 0L) }.let {}
}
