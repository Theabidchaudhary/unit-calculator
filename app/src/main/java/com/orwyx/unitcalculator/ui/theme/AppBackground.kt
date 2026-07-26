package com.orwyx.unitcalculator.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AppBackground(modifier: Modifier = Modifier) {
    val data   = LocalAppTheme.current
    val isDark = LocalNeuColors.current.isDark

    val inf = rememberInfiniteTransition(label = "blobs")

    // Each blob animates on its own period so they never sync up
    val t1 by inf.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(13_000, easing = LinearEasing), RepeatMode.Restart),
        label = "b1",
    )
    val t2 by inf.animateFloat(
        initialValue = (PI).toFloat(), targetValue = (3 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(18_000, easing = LinearEasing), RepeatMode.Restart),
        label = "b2",
    )
    val t3 by inf.animateFloat(
        initialValue = (PI / 2).toFloat(), targetValue = (5 * PI / 2).toFloat(),
        animationSpec = infiniteRepeatable(tween(24_000, easing = LinearEasing), RepeatMode.Restart),
        label = "b3",
    )

    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val r = w * 0.80f   // blob radius — large so edges are naturally soft

        // Blob 1 — floats around upper area
        val cx1 = w * 0.50f + w * 0.28f * cos(t1)
        val cy1 = h * 0.22f + h * 0.12f * sin(t1)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    data.blob1.copy(alpha = if (isDark) 0.70f else 0.55f),
                    data.blob1.copy(alpha = 0f),
                ),
                center = Offset(cx1, cy1),
                radius = r,
            ),
            radius = r,
            center = Offset(cx1, cy1),
        )

        // Blob 2 — floats around lower-left
        val cx2 = w * 0.18f + w * 0.18f * cos(t2)
        val cy2 = h * 0.75f + h * 0.14f * sin(t2)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    data.blob2.copy(alpha = if (isDark) 0.60f else 0.48f),
                    data.blob2.copy(alpha = 0f),
                ),
                center = Offset(cx2, cy2),
                radius = r * 0.85f,
            ),
            radius = r * 0.85f,
            center = Offset(cx2, cy2),
        )

        // Blob 3 — floats around right side
        val cx3 = w * 0.88f + w * 0.12f * cos(t3)
        val cy3 = h * 0.52f + h * 0.22f * sin(t3)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    data.blob3.copy(alpha = if (isDark) 0.55f else 0.42f),
                    data.blob3.copy(alpha = 0f),
                ),
                center = Offset(cx3, cy3),
                radius = r * 0.72f,
            ),
            radius = r * 0.72f,
            center = Offset(cx3, cy3),
        )
    }
}
