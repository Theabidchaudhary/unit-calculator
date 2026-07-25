package com.orwyx.unitcalculator.ui.screens.planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.engine.ForecastEngine
import com.orwyx.unitcalculator.domain.engine.PlanningEngine
import com.orwyx.unitcalculator.domain.model.DayPlan
import com.orwyx.unitcalculator.domain.model.Forecast
import com.orwyx.unitcalculator.domain.model.MeterPhase
import com.orwyx.unitcalculator.domain.model.MeterWindow
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class PlanningUiState(
    val cycleStart: LocalDate = LocalDate.now(),
    val cycleEnd: LocalDate = LocalDate.now(),
    val elapsedDays: Int = 0,
    val totalDays: Int = 30,
    val remainingDays: Int = 30,
    val totalTarget: Double = 0.0,
    val totalConsumed: Double = 0.0,
    val summaryTarget: Double = 0.0,
    val summaryConsumed: Double = 0.0,
    val days: List<DayPlan> = emptyList(),
    val meterWindows: List<MeterWindow> = emptyList(),
    val meterPhases: List<MeterPhase> = emptyList(),
    val phaseSwitchDays: Set<Int> = emptySet(),
    val forecast: Forecast = Forecast(),
    val hasMeters: Boolean = false,
) {
    val summaryExpectedToday: Double get() = summaryTarget * (elapsedDays.toFloat() / totalDays).toDouble()
    val summaryDifference: Double get() = summaryConsumed - summaryExpectedToday
    val summaryOnTrack: Boolean get() = summaryDifference <= 0
}

@HiltViewModel
class PlanningViewModel @Inject constructor(
    meterRepository: MeterRepository,
    settingsRepository: SettingsRepository,
    private val planningEngine: PlanningEngine,
    private val forecastEngine: ForecastEngine,
) : ViewModel() {

    val uiState: StateFlow<PlanningUiState> = combine(
        meterRepository.observeMeters(), settingsRepository.observeSettings(),
    ) { meters, settings ->
        val cycle = BillingCycle.of(settings.readingDate)
        val totalTarget = meters.sumOf { it.targetLimit }
        val totalConsumed = meters.sumOf { it.consumedUnits }
        val activeAndPending = meters.filter { it.closedDate == null }
        val summaryTarget = activeAndPending.sumOf { it.targetLimit }
        val summaryConsumed = activeAndPending.sumOf { it.consumedUnits }
        val windows = planningEngine.computeMeterWindows(meters, cycle)
        val phases = planningEngine.computePhases(meters, cycle)
        val switchDays = phases.dropLast(1).map { it.endDay }.toSet()
        PlanningUiState(
            cycleStart = cycle.start, cycleEnd = cycle.end,
            elapsedDays = cycle.elapsedDays, totalDays = cycle.totalDays,
            remainingDays = cycle.remainingDays,
            totalTarget = totalTarget, totalConsumed = totalConsumed,
            summaryTarget = summaryTarget, summaryConsumed = summaryConsumed,
            days = planningEngine.buildCalendar(totalTarget, totalConsumed, cycle, meters),
            meterWindows = windows,
            meterPhases = phases,
            phaseSwitchDays = switchDays,
            forecast = forecastEngine.forecast(totalConsumed, totalTarget, cycle),
            hasMeters = meters.isNotEmpty(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlanningUiState())
}
