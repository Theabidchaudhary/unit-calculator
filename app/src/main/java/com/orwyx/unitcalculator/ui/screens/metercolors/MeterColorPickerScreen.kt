package com.orwyx.unitcalculator.ui.screens.metercolors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.rounded.Check
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.ui.components.NeumorphicCard
import com.orwyx.unitcalculator.ui.theme.MeterPalette

@OptIn(ExperimentalMaterial3Api::class)
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
                        val colors = MeterPalette.pickerColors
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            colors.chunked(7).forEachIndexed { rowIdx, rowColors ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    rowColors.forEachIndexed { colIdx, color ->
                                        val index        = rowIdx * 7 + colIdx
                                        val isSelected   = index == effectiveIndex
                                        val takenByOther = otherUsedIndices.contains(index)
                                        val dotColor     = if (takenByOther) color.copy(alpha = 0.25f) else color
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .then(
                                                    if (isSelected) Modifier.shadow(
                                                        elevation    = 6.dp,
                                                        shape        = CircleShape,
                                                        ambientColor = Color.Black.copy(alpha = 0.35f),
                                                        spotColor    = Color.Black.copy(alpha = 0.45f),
                                                    ) else Modifier
                                                )
                                                .clip(CircleShape)
                                                .background(dotColor)
                                                .then(
                                                    if (isSelected)
                                                        Modifier.border(2.5.dp, Color.White, CircleShape)
                                                    else Modifier
                                                )
                                                .then(
                                                    if (!takenByOther)
                                                        Modifier.clickable { viewModel.setColor(entry.meter.id, index) }
                                                    else Modifier
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    Icons.Rounded.Check,
                                                    contentDescription = null,
                                                    tint     = Color.White,
                                                    modifier = Modifier.size(14.dp),
                                                )
                                            }
                                        }
                                    }
                                    // fill remaining slots in last row
                                    repeat(7 - rowColors.size) {
                                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
