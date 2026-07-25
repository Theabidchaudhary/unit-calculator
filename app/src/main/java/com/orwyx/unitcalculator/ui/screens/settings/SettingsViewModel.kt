package com.orwyx.unitcalculator.ui.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.backup.BackupManager
import com.orwyx.unitcalculator.backup.BackupResult
import com.orwyx.unitcalculator.domain.model.AppSettings
import com.orwyx.unitcalculator.domain.model.ThemeMode
import com.orwyx.unitcalculator.domain.repository.HistoryRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val backupManager: BackupManager,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    /** One-shot user feedback for backup/restore actions; cleared after the UI shows it. */
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun setTheme(mode: ThemeMode) = viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    fun setReadingDate(day: Int) = viewModelScope.launch { settingsRepository.setReadingDate(day) }
    fun setDefaultTarget(target: Double) = viewModelScope.launch { settingsRepository.setDefaultTarget(target) }
    fun setAllowDecimals(allow: Boolean) = viewModelScope.launch { settingsRepository.setAllowDecimals(allow) }
    fun clearHistory() = viewModelScope.launch { historyRepository.clearAll() }

    fun consumeMessage() { _message.value = null }

    /** Writes a JSON backup to the user-chosen [uri]. */
    fun exportBackup(uri: Uri) = viewModelScope.launch {
        val result = runCatching {
            val content = backupManager.export()
            withContext(Dispatchers.IO) {
                context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray()) }
                    ?: error("Couldn't open the selected location.")
            }
        }
        _message.value = result.fold(
            onSuccess = { "Backup saved successfully." },
            onFailure = { "Backup failed: ${it.message}" },
        )
    }

    /** Restores from the JSON backup at the user-chosen [uri]. */
    fun importBackup(uri: Uri) = viewModelScope.launch {
        val content = runCatching {
            withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Couldn't read the selected file.")
            }
        }.getOrElse {
            _message.value = "Import failed: ${it.message}"
            return@launch
        }
        _message.value = when (val result = backupManager.import(content)) {
            is BackupResult.Success ->
                "Restored ${result.meterCount} meters and ${result.historyCount} history records."
            is BackupResult.Error -> result.message
        }
    }
}
