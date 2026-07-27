package com.orwyx.unitcalculator.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun AppBackground(modifier: Modifier = Modifier) {
    val primary    = MaterialTheme.colorScheme.primary
    val secondary  = MaterialTheme.colorScheme.secondary
    val background = MaterialTheme.colorScheme.background
    val isDark     = LocalNeuColors.current.isDark

    // Blob alphas — subtle in light mode, slightly stronger in dark
    val alpha1 = if (isDark) 0.30f else 0.16f
    val alpha2 = if (isDark) 0.22f else 0.12f
    val alpha3 = if (isDark) 0.14f else 0.08f

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(color = background)

        // Blob 1 — upper right
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = alpha1), Color.Transparent),
                center = Offset(w * 0.88f, h * 0.12f),
                radius = w * 0.75f,
            ),
            radius = w * 0.75f,
            center = Offset(w * 0.88f, h * 0.12f),
        )

        // Blob 2 — lower left
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = alpha2), Color.Transparent),
                center = Offset(w * 0.12f, h * 0.88f),
                radius = w * 0.70f,
            ),
            radius = w * 0.70f,
            center = Offset(w * 0.12f, h * 0.88f),
        )

        // Blob 3 — center accent
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = alpha3), Color.Transparent),
                center = Offset(w * 0.50f, h * 0.48f),
                radius = w * 0.55f,
            ),
            radius = w * 0.55f,
            center = Offset(w * 0.50f, h * 0.48f),
        )
    }
}
