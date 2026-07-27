package com.orwyx.unitcalculator.ui.screens.meters

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.core.util.Formatters
import androidx.compose.ui.graphics.Color
import com.orwyx.unitcalculator.ui.components.AnimatedProgressBar
import com.orwyx.unitcalculator.ui.components.ConfirmDialog
import com.orwyx.unitcalculator.ui.components.NeumorphicCard
import com.orwyx.unitcalculator.ui.components.SectionHeader
import com.orwyx.unitcalculator.ui.components.StatusBadge
import com.orwyx.unitcalculator.ui.theme.StatusDeepGreen
import com.orwyx.unitcalculator.ui.theme.StatusRed
import com.orwyx.unitcalculator.ui.theme.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeterDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: MeterDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val meter = state.meter
    var showReset by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(meter?.name ?: "Meter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDelete = true }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor          = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor     = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { padding ->
        if (meter == null) {
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("Ref", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(meter.referenceNumber, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            }
                            StatusBadge(meter.status)
                        }
                        Spacer(Modifier.height(16.dp))
                        AnimatedProgressBar(fraction = meter.usedFraction)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailStat("Previous", Formatters.units(meter.previousReading))
                            DetailStat("Current", Formatters.units(meter.currentReading))
                            DetailStat("Consumed", Formatters.units(meter.consumedUnits))
                            DetailStat("Remaining", Formatters.units(meter.remainingUnits))
                        }
                    }
                }
            }

            item { SectionHeader("Forecast") }
            item {
                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DetailStat("Avg / day", Formatters.units(state.avgDailyUsage))
                        DetailStat("Estimate (${state.projectedPeriodDays}d)", Formatters.units(state.projectedMonthEnd))
                        DetailStat(
                            if (state.projectedOverage > 0) "Over" else "Under",
                            Formatters.units(kotlin.math.abs(state.projectedOverage)),
                            valueColor = if (state.projectedOverage > 0) StatusRed else StatusDeepGreen,
                        )
                    }
                }
            }

            item {
                val isClosed = meter.closedDate != null
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val resetInteraction = remember { MutableInteractionSource() }
                    Button(
                        onClick = { showReset = true },
                        enabled = !isClosed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .pressScale(resetInteraction),
                        interactionSource = resetInteraction,
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Icon(Icons.Rounded.RestartAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Reset Month", style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(
                        onClick = { meter?.let { onEdit(it.id) } },
                        enabled = meter?.closedDate == null,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                        Text(" Edit")
                    }
                }
            }

        }
    }

    if (showReset) {
        ConfirmDialog(
            title = "Reset this month?",
            message = "The current month will be saved to history and the cycle rolls forward.",
            confirmLabel = "Reset",
            onConfirm = { viewModel.reset(); showReset = false },
            onDismiss = { showReset = false },
        )
    }
    if (showDelete) {
        ConfirmDialog(
            title = "Delete meter?",
            message = "This permanently removes the meter and all of its history.",
            confirmLabel = "Delete",
            onConfirm = { showDelete = false; viewModel.delete(onBack) },
            onDismiss = { showDelete = false },
        )
    }
}

@Composable
private fun DetailStat(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = valueColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

