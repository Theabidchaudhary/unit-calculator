package com.orwyx.unitcalculator.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orwyx.unitcalculator.domain.model.ReadingHistory
import com.orwyx.unitcalculator.domain.repository.HistoryRepository
import com.orwyx.unitcalculator.domain.repository.MeterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** A history record paired with its meter's current name for display. */
data class HistoryListItem(
    val record: ReadingHistory,
    val meterName: String,
)

data class HistoryUiState(
    val items: List<HistoryListItem> = emptyList(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    meterRepository: MeterRepository,
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = combine(
        historyRepository.observeAll(),
        meterRepository.observeMeters(),
    ) { history, meters ->
        val names = meters.associate { it.id to it.name }
        HistoryUiState(
            items = history.map { HistoryListItem(it, names[it.meterId] ?: "Deleted meter") },
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun delete(id: Long) = viewModelScope.launch { historyRepository.delete(id) }
    fun clearAll() = viewModelScope.launch { historyRepository.clearAll() }
}
