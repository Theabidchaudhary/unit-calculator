package com.orwyx.unitcalculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.ConsumptionColors

/**
 * A rounded progress bar whose fill both animates in width and shifts colour along the
 * consumption gradient (deep green -> dark red) as the fraction rises.
 */
@Composable
fun AnimatedProgressBar(
    fraction: Float,
    modifier: Modifier = Modifier,
    trackColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant,
    heightDp: Int = 12,
) {
    val animated by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "progressFraction",
    )
    val endColor = ConsumptionColors.colorFor(fraction)
    val startColor = ConsumptionColors.colorFor(fraction * 0.4f)

    Canvas(
        modifier
            .fillMaxWidth()
            .height(heightDp.dp),
    ) {
        val radius = CornerRadius(size.height / 2f, size.height / 2f)
        // Track
        drawRoundRect(color = trackColor, cornerRadius = radius)
        // Fill
        val fillWidth = size.width * animated
        if (fillWidth > 0f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(startColor, endColor),
                    startX = 0f,
                    endX = fillWidth,
                ),
                topLeft = Offset.Zero,
                size = Size(fillWidth.coerceAtLeast(size.height), size.height),
                cornerRadius = radius,
            )
        }
    }
}
