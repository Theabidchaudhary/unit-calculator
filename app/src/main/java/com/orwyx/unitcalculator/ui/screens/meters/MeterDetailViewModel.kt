package com.orwyx.unitcalculator.ui.screens.meters

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.engine.CalculationEngine
import com.orwyx.unitcalculator.domain.model.AppSettings
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.ReadingHistory
import com.orwyx.unitcalculator.domain.repository.HistoryRepository
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import com.orwyx.unitcalculator.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class MeterDetailUiState(
    val meter: Meter? = null,
    val history: List<ReadingHistory> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val avgDailyUsage: Double = 0.0,
    val projectedMonthEnd: Double = 0.0,
    val projectedOverage: Double = 0.0,
)

@HiltViewModel
class MeterDetailViewModel @Inject constructor(
    private val meterRepository: MeterRepository,
    historyRepository: HistoryRepository,
    settingsRepository: SettingsRepository,
    private val calculationEngine: CalculationEngine,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val meterId: Long = savedStateHandle.get<String>(Routes.ARG_METER_ID)?.toLongOrNull() ?: 0L

    val uiState: StateFlow<MeterDetailUiState> = combine(
        meterRepository.observeMeter(meterId),
        historyRepository.observeForMeter(meterId),
        settingsRepository.observeSettings(),
    ) { meter, history, settings ->
        val cycle = BillingCycle.of(settings.readingDate)
        MeterDetailUiState(
            meter = meter,
            history = history,
            settings = settings,
            avgDailyUsage = meter?.let { calculationEngine.averageDailyUsage(it, cycle) } ?: 0.0,
            projectedMonthEnd = meter?.let { calculationEngine.projectedMonthEnd(it, cycle) } ?: 0.0,
            projectedOverage = meter?.let { calculationEngine.projectedOverage(it, cycle) } ?: 0.0,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MeterDetailUiState())

    fun reset() = viewModelScope.launch {
        val state = uiState.value
        val meter = state.meter ?: return@launch
        val cycle = BillingCycle.of(state.settings.readingDate)
        meterRepository.resetMonth(
            id = meter.id,
            monthLabel = monthLabel(cycle.start),
            avgDailyUsage = calculationEngine.averageDailyUsage(meter, cycle),
            closedAt = System.currentTimeMillis(),
        )
    }

    fun delete(onDeleted: () -> Unit) = viewModelScope.launch {
        meterRepository.delete(meterId)
        onDeleted()
    }

    private fun monthLabel(start: LocalDate): String {
        val ym = YearMonth.from(start)
        return "${ym.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${ym.year}"
    }
}
