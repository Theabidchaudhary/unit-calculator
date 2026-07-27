package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    hazeState: HazeState,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark   = LocalNeuColors.current.isDark
    val barColor = if (isDark) Color.Black.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.70f)

    CenterAlignedTopAppBar(
        title   = { Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = onSettings) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings")
            }
        },
        colors  = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor         = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        modifier = modifier
            .fillMaxWidth()
            .hazeChild(
                state = hazeState,
                style = HazeStyle(
                    backgroundColor = barColor,
                    tint            = null,
                    blurRadius      = 24.dp,
                ),
            ),
    )
}
