package com.orwyx.unitcalculator.ui.screens.meters

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ElectricMeter
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.core.util.Formatters
import com.orwyx.unitcalculator.domain.model.Meter
import com.orwyx.unitcalculator.ui.components.ConfirmDialog
import com.orwyx.unitcalculator.ui.components.CountChip
import com.orwyx.unitcalculator.ui.components.EmptyState
import com.orwyx.unitcalculator.ui.components.MeterCard
import com.orwyx.unitcalculator.ui.components.SearchSortBar
import com.orwyx.unitcalculator.ui.components.SectionHeader
import com.orwyx.unitcalculator.ui.components.SummaryCard
import com.orwyx.unitcalculator.ui.theme.StatusDeepGreen
import com.orwyx.unitcalculator.ui.theme.StatusOrange
import com.orwyx.unitcalculator.ui.theme.StatusRed
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MetersScreen(
    onOpenMeter: (Long) -> Unit,
    contentPadding: PaddingValues,
    viewModel: MetersViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var meterToReset by remember { mutableStateOf<Meter?>(null) }
    var showResetAll by remember { mutableStateOf(false) }
    val itemHeights = remember { mutableStateMapOf<Long, Int>() }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 120.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isEmpty) {
                item {
                    EmptyState(
                        icon = Icons.Rounded.ElectricMeter,
                        title = "No meters yet",
                        message = "Tap the + button to add your first electricity meter.",
                    )
                }
                return@LazyColumn
            }

            item { SectionHeader("Overview") }
            item { DashboardRow(state, onResetAll = { showResetAll = true }) }

            if (!state.reorderMode) {
                item {
                    SearchSortBar(
                        query = state.query,
                        onQueryChange = viewModel::onQueryChange,
                        sort = state.sort,
                        onSortChange = viewModel::onSortChange,
                    )
                }
            }

            item { SectionHeader("Meters") }

            items(state.meters, key = { it.id }) { meter ->
                val seqNum = state.sequenceNumberFor(meter.id)
                val phase = state.phaseFor(meter.id)

                MeterCard(
                    meter = meter,
                    sequenceNumber = seqNum,
                    phase = phase,
                    remainingDays = state.remainingDays,
                    allowDecimals = state.settings.allowDecimals,
                    isActive = state.settings.activeMeterId == meter.id,
                    isClosed = meter.closedDate != null,
                    modifier = Modifier
                        .animateItem()
                        .onGloballyPositioned { coords -> itemHeights[meter.id] = coords.size.height }
                        .pointerInput(state.reorderMode) {
                            if (!state.reorderMode) {
                                coroutineScope {
                                    while (true) {
                                        awaitPointerEventScope { awaitFirstDown(requireUnconsumed = false) }
                                        val job = launch {
                                            delay(1_500L)
                                            viewModel.enterReorderMode()
                                        }
                                        try {
                                            awaitPointerEventScope { waitForUpOrCancellation() }
                                        } finally {
                                            job.cancel()
                                        }
                                    }
                                }
                            } else {
                                while (true) {
                                    var accumulated = 0f
                                    detectDragGestures(
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            accumulated += dragAmount.y
                                            val threshold = (itemHeights[meter.id] ?: 300) * 0.5f
                                            when {
                                                accumulated > threshold -> {
                                                    viewModel.moveDown(meter.id)
                                                    accumulated = 0f
                                                }
                                                accumulated < -threshold -> {
                                                    viewModel.moveUp(meter.id)
                                                    accumulated = 0f
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        },
                    reorderMode = state.reorderMode,
                    onClick = { onOpenMeter(meter.id) },
                    onCurrentReadingSubmit = { m, raw ->
                        viewModel.updateCurrentReading(m, raw, state.settings.allowDecimals)
                    },
                    onToggleActive = { viewModel.toggleActiveMeter(meter) },
                    onSetClosedDate = { date -> viewModel.setMeterClosedDate(meter, date) },
                )
            }
        }

        if (state.reorderMode) {
            Button(
                onClick = viewModel::savePendingOrder,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = contentPadding.calculateBottomPadding() + 80.dp),
            ) {
                Text("Save Order")
            }
        }
    }

    meterToReset?.let { meter ->
        ConfirmDialog(
            title = "Reset ${meter.name}?",
            message = "This closes the current month and saves it to history. The current reading becomes the new previous reading.",
            confirmLabel = "Reset",
            onConfirm = { viewModel.resetMeter(meter, state.settings); meterToReset = null },
            onDismiss = { meterToReset = null },
        )
    }

    if (showResetAll) {
        ConfirmDialog(
            title = "Reset all meters?",
            message = "This closes the billing cycle for every meter and archives current readings as history. All meters will unlock and start fresh from meter #1.",
            confirmLabel = "Reset All",
            onConfirm = { viewModel.resetAllMeters(state.settings); showResetAll = false },
            onDismiss = { showResetAll = false },
        )
    }
}

@Composable
private fun DashboardRow(state: MetersUiState, onResetAll: () -> Unit) {
    val s = state.summary
    Column {
        val cards = listOf(
            Triple(Icons.Rounded.ElectricMeter, s.totalMeters.toString(), "Total meters" to StatusDeepGreen),
            Triple(Icons.Rounded.Bolt, Formatters.units(s.totalConsumed), "Units consumed" to StatusOrange),
            Triple(Icons.Rounded.Savings, Formatters.units(s.totalRemaining), "Units remaining" to StatusDeepGreen),
            Triple(Icons.Rounded.BatteryChargingFull, Formatters.units(s.avgDailyUsage), "Avg / day" to StatusOrange),
            Triple(Icons.Rounded.TrendingUp, Formatters.units(s.projectedMonthEnd), "Projected end" to StatusRed),
            Triple(Icons.Rounded.Insights, Formatters.percent(s.overallFraction), "Overall used" to StatusRed),
        )
        cards.chunked(3).forEach { rowCards ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowCards.forEach { (icon, value, captionAndColor) ->
                    val (caption, accent) = captionAndColor
                    SummaryCard(icon = icon, value = value, caption = caption, accent = accent, modifier = Modifier.weight(1f))
                }
                repeat(3 - rowCards.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(10.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CountChip("Safe", s.safeCount, StatusDeepGreen)
                CountChip("Warning", s.warningCount, StatusOrange)
                CountChip("Critical", s.criticalCount, StatusRed)
            }
            TextButton(onClick = onResetAll) {
                Text("Reset cycle", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
