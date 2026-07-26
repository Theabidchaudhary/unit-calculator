package com.orwyx.unitcalculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.domain.model.AccentColor
import com.orwyx.unitcalculator.domain.model.AppTheme
import com.orwyx.unitcalculator.domain.model.ThemeMode
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = settingsRepository.observeSettings()
        .map { it.themeMode }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val accentColor: StateFlow<AccentColor> = settingsRepository.observeSettings()
        .map { it.accentColor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AccentColor.BLUE)

    val appTheme: StateFlow<AppTheme> = settingsRepository.observeSettings()
        .map { it.appTheme }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppTheme.SUNSET)
}
