package com.orwyx.unitcalculator.ui.screens.meters

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.domain.engine.MeterValidator
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterInput
import com.orwyx.unitcalculator.domain.model.MeterInputErrors
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import com.orwyx.unitcalculator.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MeterEditUiState(
    val input: MeterInput = MeterInput(),
    val errors: MeterInputErrors = MeterInputErrors(),
    val allowDecimals: Boolean = false,
    val isEditing: Boolean = false,
    val isLoaded: Boolean = false,
    val saved: Boolean = false,
) {
    val title: String get() = if (isEditing) "Edit meter" else "New meter"
}

@HiltViewModel
class MeterEditViewModel @Inject constructor(
    private val meterRepository: MeterRepository,
    private val settingsRepository: SettingsRepository,
    private val validator: MeterValidator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val meterId: Long = savedStateHandle.get<String>(Routes.ARG_METER_ID)?.toLongOrNull() ?: 0L

    private val _state = MutableStateFlow(MeterEditUiState())
    val state: StateFlow<MeterEditUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = settingsRepository.observeSettings().first()
            if (meterId != 0L) {
                meterRepository.getMeter(meterId)?.let { meter ->
                    _state.value = MeterEditUiState(
                        input = meter.toInput(),
                        allowDecimals = settings.allowDecimals,
                        isEditing = true,
                        isLoaded = true,
                    )
                }
            } else {
                _state.value = MeterEditUiState(
                    input = MeterInput(targetLimit = trimZero(settings.defaultTarget)),
                    allowDecimals = settings.allowDecimals,
                    isEditing = false,
                    isLoaded = true,
                )
            }
        }
    }

    fun update(transform: (MeterInput) -> MeterInput) {
        _state.value = _state.value.copy(input = transform(_state.value.input), errors = MeterInputErrors())
    }

    /** Validates and saves. Returns via [MeterEditUiState.saved]; caller navigates back on true. */
    fun save() {
        val current = _state.value
        val effectiveInput = current.input.copy(currentReading = current.input.previousReading)
        val errors = validator.validate(effectiveInput, current.allowDecimals)
        if (!errors.isValid) {
            _state.value = current.copy(errors = errors)
            return
        }
        viewModelScope.launch {
            val toSave = if (current.isEditing) {
                val existing = meterRepository.getMeter(effectiveInput.id)
                effectiveInput.toMeter().copy(
                    currentReading = existing?.currentReading ?: effectiveInput.previousReading.toDoubleOrNull() ?: 0.0,
                    closedDate = existing?.closedDate,
                )
            } else {
                effectiveInput.toMeter()
            }
            meterRepository.upsert(toSave)
            _state.value = current.copy(saved = true)
        }
    }

    private fun Meter.toInput() = MeterInput(
        id = id,
        name = name,
        referenceNumber = referenceNumber,
        providerId = providerId,
        targetLimit = trimZero(targetLimit),
        previousReading = trimZero(previousReading),
        currentReading = trimZero(currentReading),
    )

    private fun MeterInput.toMeter() = Meter(
        id = id,
        name = name.trim(),
        referenceNumber = referenceNumber.trim(),
        providerId = providerId,
        targetLimit = targetLimit.toDoubleOrNull() ?: 0.0,
        previousReading = previousReading.toDoubleOrNull() ?: 0.0,
        currentReading = currentReading.toDoubleOrNull() ?: 0.0,
    )

    private fun trimZero(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
}
