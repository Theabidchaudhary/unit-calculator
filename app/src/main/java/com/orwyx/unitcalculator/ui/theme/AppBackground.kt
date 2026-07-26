package com.orwyx.unitcalculator.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Warm amber/orange accent matching the room lighting
val WarmAccent = Color(0xFFE87C1A)

@Composable
fun AppBackground(modifier: Modifier = Modifier) {
    val isDark = LocalNeuColors.current.isDark

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Deep warm dark base — like the darkened room interior
        drawRect(color = Color(0xFF1A0800))

        // Main warm orange glow — right side, like the tall neon strip in the image
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE87C1A).copy(alpha = 0.90f), Color.Transparent),
                center = Offset(w * 1.05f, h * 0.28f),
                radius = w * 1.10f,
            ),
            radius = w * 1.10f,
            center = Offset(w * 1.05f, h * 0.28f),
        )

        // Amber glow — lower right, like the curved chair base lighting
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF9933).copy(alpha = 0.65f), Color.Transparent),
                center = Offset(w * 0.90f, h * 0.88f),
                radius = w * 0.85f,
            ),
            radius = w * 0.85f,
            center = Offset(w * 0.90f, h * 0.88f),
        )

        // Warm sienna fill — center, adds depth like the sofa area
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF7A3010).copy(alpha = 0.55f), Color.Transparent),
                center = Offset(w * 0.38f, h * 0.60f),
                radius = w * 0.75f,
            ),
            radius = w * 0.75f,
            center = Offset(w * 0.38f, h * 0.60f),
        )

        // Subtle warm top-center glow (lamp above)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFCC5500).copy(alpha = 0.35f), Color.Transparent),
                center = Offset(w * 0.35f, h * 0.08f),
                radius = w * 0.55f,
            ),
            radius = w * 0.55f,
            center = Offset(w * 0.35f, h * 0.08f),
        )

        // Dark mode: overlay 35% black to deepen the scene
        if (isDark) {
            drawRect(color = Color.Black.copy(alpha = 0.35f))
        }
    }
}
