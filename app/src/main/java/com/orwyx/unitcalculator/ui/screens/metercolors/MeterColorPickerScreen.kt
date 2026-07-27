package com.orwyx.unitcalculator.ui.screens.metercolors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.ui.components.NeumorphicCard
import com.orwyx.unitcalculator.ui.theme.MeterPalette

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MeterColorPickerScreen(
    onBack: () -> Unit,
    viewModel: MeterColorPickerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Meter Colors", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor          = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Choose a color for each meter. It will appear on the planning calendar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(4.dp))
            }
            items(state.entries) { entry ->
                val entryIndex       = state.entries.indexOf(entry)
                val effectiveIndex   = entry.colorIndex ?: (entryIndex % MeterPalette.pickerColors.size)
                // Include default-assigned colors as "used" so other meters can't pick them
                val allEffective     = state.entries.mapIndexed { i, e -> e.colorIndex ?: (i % MeterPalette.pickerColors.size) }.toSet()
                val otherUsedIndices = allEffective - setOf(effectiveIndex)

                NeumorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(entry.meter.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(10.dp))
                        FlowRow(
                            maxItemsInEachRow     = 7,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement   = Arrangement.spacedBy(10.dp),
                        ) {
                            MeterPalette.pickerColors.forEachIndexed { index, color ->
                                val isSelected   = index == effectiveIndex
                                val takenByOther = otherUsedIndices.contains(index)
                                val dotColor     = if (takenByOther) color.copy(alpha = 0.25f) else color
                                val borderColor  = when {
                                    takenByOther -> Color.White.copy(alpha = 0.20f)
                                    isSelected   -> Color.White
                                    else         -> Color.White.copy(alpha = 0.70f)
                                }
                                val borderWidth  = if (isSelected) 2.5.dp else 1.5.dp
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                        .border(borderWidth, borderColor, CircleShape)
                                        .then(
                                            if (!takenByOther)
                                                Modifier.clickable { viewModel.setColor(entry.meter.id, index) }
                                            else
                                                Modifier
                                        ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
