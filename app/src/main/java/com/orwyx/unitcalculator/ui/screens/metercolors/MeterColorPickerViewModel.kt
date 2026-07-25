package com.orwyx.unitcalculator.ui.screens.metercolors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MeterColorEntry(val meter: Meter, val colorIndex: Int?)

data class MeterColorPickerUiState(
    val entries: List<MeterColorEntry> = emptyList(),
)

@HiltViewModel
class MeterColorPickerViewModel @Inject constructor(
    private val meterRepository: MeterRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val uiState: StateFlow<MeterColorPickerUiState> = combine(
        meterRepository.observeMeters(),
        settingsRepository.observeSettings(),
    ) { meters, settings ->
        MeterColorPickerUiState(
            entries = meters.map { meter ->
                MeterColorEntry(meter, settings.meterColors[meter.id])
            },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MeterColorPickerUiState())

    fun setColor(meterId: Long, colorIndex: Int) = viewModelScope.launch {
        settingsRepository.setMeterColor(meterId, colorIndex)
    }
}
