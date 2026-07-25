package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, onSettings: () -> Unit) {
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val neu = LocalNeuColors.current
    val sheen = lerp(surface, surfaceVariant, 0.35f).copy(alpha = 0.95f)
    val backgroundBrush = Brush.verticalGradient(colors = listOf(sheen, surface))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, ambientColor = neu.shadow.copy(alpha = 0.40f), spotColor = neu.shadow.copy(alpha = 0.40f))
            .background(backgroundBrush)
            .drawBehind {
                drawRect(color = onSurface.copy(alpha = 0.08f), topLeft = Offset(0f, size.height - 1f), size = Size(size.width, 1f))
            },
    ) {
        CenterAlignedTopAppBar(
            title = { Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge) },
            actions = {
                IconButton(onClick = onSettings) { Icon(Icons.Rounded.Settings, contentDescription = "Settings") }
            },
            colors = transparentTopBarColors(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun transparentTopBarColors(): TopAppBarColors =
    TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent)

private fun lerp(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = a.alpha + (b.alpha - a.alpha) * tt,
    )
}
