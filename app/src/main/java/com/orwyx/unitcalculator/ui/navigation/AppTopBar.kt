package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors
import com.orwyx.unitcalculator.ui.theme.frostedBlurBackground
import com.orwyx.unitcalculator.ui.theme.glassBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onSettings: () -> Unit) {
    val isDark = LocalNeuColors.current.isDark
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassBar(isDark),
    ) {
        // Blurred background layer (blurs only this layer's own fill, not the sharp content above)
        Box(
            modifier = Modifier
                .matchParentSize()
                .frostedBlurBackground(isDark),
        )
        // Sharp content on top of the blurred background
        CenterAlignedTopAppBar(
            title = { Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
            actions = {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                }
            },
            colors = transparentTopBarColors(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun transparentTopBarColors(): TopAppBarColors =
    TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor         = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
    )
