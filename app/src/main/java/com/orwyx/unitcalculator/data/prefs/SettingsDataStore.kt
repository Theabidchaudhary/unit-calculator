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
import com.orwyx.unitcalculator.domain.model.AccentColor
import com.orwyx.unitcalculator.domain.model.AppSettings
import com.orwyx.unitcalculator.domain.model.AppTheme
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
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val METER_COLORS = stringPreferencesKey("meter_colors")
        val APP_THEME = stringPreferencesKey("app_theme")
        val CYCLE_RESET_DAY = longPreferencesKey("cycle_reset_day")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            readingDate = prefs[Keys.READING_DATE] ?: 1,
            defaultTarget = prefs[Keys.DEFAULT_TARGET] ?: 200.0,
            allowDecimals = prefs[Keys.ALLOW_DECIMALS] ?: false,
            activeMeterId = prefs[Keys.ACTIVE_METER_ID]?.takeIf { it > 0L },
            accentColor = prefs[Keys.ACCENT_COLOR]?.let { runCatching { AccentColor.valueOf(it) }.getOrNull() } ?: AccentColor.BLUE,
            meterColors = decodeMeterColors(prefs[Keys.METER_COLORS] ?: ""),
            appTheme = prefs[Keys.APP_THEME]?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() } ?: AppTheme.SUNSET,
            cycleResetDay = prefs[Keys.CYCLE_RESET_DAY] ?: 0L,
        )
    }

    suspend fun setTheme(mode: ThemeMode) = context.dataStore.edit { it[Keys.THEME] = mode.name }.let {}
    suspend fun setReadingDate(day: Int) = context.dataStore.edit { it[Keys.READING_DATE] = day.coerceIn(1, 31) }.let {}
    suspend fun setDefaultTarget(target: Double) = context.dataStore.edit { it[Keys.DEFAULT_TARGET] = target }.let {}
    suspend fun setAllowDecimals(allow: Boolean) = context.dataStore.edit { it[Keys.ALLOW_DECIMALS] = allow }.let {}
    suspend fun setActiveMeterId(id: Long?) = context.dataStore.edit { it[Keys.ACTIVE_METER_ID] = (id ?: 0L) }.let {}
    suspend fun setAccentColor(color: AccentColor) = context.dataStore.edit { it[Keys.ACCENT_COLOR] = color.name }.let {}
    suspend fun setAppTheme(theme: AppTheme) = context.dataStore.edit { it[Keys.APP_THEME] = theme.name }.let {}
    suspend fun setCycleResetDay(day: Long) = context.dataStore.edit { it[Keys.CYCLE_RESET_DAY] = day }.let {}

    suspend fun setMeterColor(meterId: Long, colorIndex: Int) = context.dataStore.edit { prefs ->
        val current = decodeMeterColors(prefs[Keys.METER_COLORS] ?: "").toMutableMap()
        current[meterId] = colorIndex
        prefs[Keys.METER_COLORS] = encodeMeterColors(current)
    }.let {}

    private fun encodeMeterColors(map: Map<Long, Int>): String =
        map.entries.joinToString(";") { "${it.key}=${it.value}" }

    private fun decodeMeterColors(s: String): Map<Long, Int> =
        if (s.isBlank()) emptyMap()
        else s.split(";").mapNotNull { entry ->
            val parts = entry.split("=")
            if (parts.size == 2) parts[0].toLongOrNull()?.let { id -> parts[1].toIntOrNull()?.let { idx -> id to idx } }
            else null
        }.toMap()
}
