package com.orwyx.unitcalculator.ui.screens.meters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.core.util.BillingCycle
import com.orwyx.unitcalculator.domain.engine.CalculationEngine
import com.orwyx.unitcalculator.domain.engine.PlanningEngine
import com.orwyx.unitcalculator.domain.model.AppSettings
import com.orwyx.unitcalculator.domain.model.DashboardSummary
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.domain.model.MeterPhase
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import com.orwyx.unitcalculator.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class MetersUiState(
    val meters: List<Meter> = emptyList(),
    val summary: DashboardSummary = DashboardSummary(),
    val settings: AppSettings = AppSettings(),
    val query: String = "",
    val sort: MeterSort = MeterSort.SEQUENCE,
    val remainingDays: Int = 0,
    val isLoading: Boolean = true,
    val reorderMode: Boolean = false,
    val sequenceOrder: List<Long> = emptyList(),
    val phasesById: Map<Long, MeterPhase> = emptyMap(),
) {
    val isEmpty: Boolean get() = !isLoading && meters.isEmpty()
    fun sequenceNumberFor(meterId: Long): Int = sequenceOrder.indexOf(meterId) + 1
    fun phaseFor(meterId: Long): MeterPhase? = phasesById[meterId]
}

@HiltViewModel
class MetersViewModel @Inject constructor(
    private val meterRepository: MeterRepository,
    private val settingsRepository: SettingsRepository,
    private val calculationEngine: CalculationEngine,
    private val planningEngine: PlanningEngine,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val sort = MutableStateFlow(MeterSort.SEQUENCE)
    private val reorderMode = MutableStateFlow(false)
    private val pendingOrder = MutableStateFlow<List<Long>?>(null)

    val uiState: StateFlow<MetersUiState> = combine(
        meterRepository.observeMeters(),
        settingsRepository.observeSettings(),
        combine(query, sort) { q, s -> q to s },
        combine(reorderMode, pendingOrder) { rm, po -> rm to po },
    ) { meters, settings, qs, rp ->
        val q = qs.first
        val s = qs.second
        val rm = rp.first
        val po = rp.second
        val cycle = BillingCycle.of(settings.readingDate)
        val phases = planningEngine.computePhases(meters, cycle)
        val orderedIds = po ?: meters.map { it.id }
        val metersById = meters.associateBy { it.id }
        MetersUiState(
            meters = if (rm) orderedIds.mapNotNull { metersById[it] } else s.sort(meters.filterByQuery(q)),
            summary = calculationEngine.summarize(meters, cycle),
            settings = settings,
            query = q,
            sort = s,
            remainingDays = cycle.remainingDays,
            isLoading = false,
            reorderMode = rm,
            sequenceOrder = orderedIds,
            phasesById = phases.associateBy { it.meter.id },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MetersUiState(),
    )

    fun onQueryChange(value: String) { query.value = value }
    fun onSortChange(value: MeterSort) { sort.value = value }

    fun enterReorderMode() {
        pendingOrder.value = uiState.value.sequenceOrder.toList()
        reorderMode.value = true
    }

    fun exitReorderMode() {
        pendingOrder.value = null
        reorderMode.value = false
    }

    fun savePendingOrder() = viewModelScope.launch {
        pendingOrder.value?.let { order -> meterRepository.reorderMeters(order) }
        pendingOrder.value = null
        reorderMode.value = false
    }

    fun moveUp(meterId: Long) {
        val current = pendingOrder.value?.toMutableList() ?: return
        val idx = current.indexOf(meterId)
        if (idx > 0) {
            current.add(idx - 1, current.removeAt(idx))
            pendingOrder.value = current
        }
    }

    fun moveDown(meterId: Long) {
        val current = pendingOrder.value?.toMutableList() ?: return
        val idx = current.indexOf(meterId)
        if (idx < current.lastIndex) {
            current.add(idx + 1, current.removeAt(idx))
            pendingOrder.value = current
        }
    }

    fun updateCurrentReading(meter: Meter, rawReading: String, allowDecimals: Boolean) {
        val parsed = rawReading.toDoubleOrNull() ?: return
        if (parsed < 0.0) return
        if (!allowDecimals && rawReading.contains('.')) return
        if (parsed < meter.previousReading) return
        viewModelScope.launch { meterRepository.upsert(meter.copy(currentReading = parsed)) }
    }

    fun toggleActiveMeter(meter: Meter) {
        viewModelScope.launch {
            val current = settingsRepository.observeSettings().first().activeMeterId
            val newId = if (current == meter.id) null else meter.id
            settingsRepository.setActiveMeterId(newId)
        }
    }

    fun setMeterClosedDate(meter: Meter, date: LocalDate?) {
        viewModelScope.launch { meterRepository.setClosedDate(meter.id, date) }
    }

    fun deleteMeter(id: Long) = viewModelScope.launch { meterRepository.delete(id) }

    fun resetMeter(meter: Meter, settings: AppSettings) = viewModelScope.launch {
        val cycle = BillingCycle.of(settings.readingDate)
        val avgDaily = calculationEngine.averageDailyUsage(meter, cycle)
        meterRepository.resetMonth(
            id = meter.id,
            monthLabel = currentMonthLabel(cycle.start),
            avgDailyUsage = avgDaily,
            closedAt = System.currentTimeMillis(),
        )
    }

    fun resetAllMeters(settings: AppSettings) = viewModelScope.launch {
        val cycle = BillingCycle.of(settings.readingDate)
        meterRepository.resetAllMeters(
            monthLabel = currentMonthLabel(cycle.start),
            closedAt = System.currentTimeMillis(),
        )
    }

    private fun currentMonthLabel(start: LocalDate): String {
        val ym = YearMonth.from(start)
        val month = ym.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        return "$month ${ym.year}"
    }
}
