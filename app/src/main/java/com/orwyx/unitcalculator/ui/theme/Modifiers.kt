package com.orwyx.unitcalculator.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.neumorphic(
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    elevation: Dp = 10.dp,
    surface: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
): Modifier {
    val neu = LocalNeuColors.current
    return this
        .shadow(elevation = elevation, shape = shape, ambientColor = neu.shadow, spotColor = neu.shadow)
        .drawBehind { drawRoundRectHighlight(neu.highlight) }
        .background(color = surface, shape = shape)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRoundRectHighlight(highlight: Color) {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(highlight.copy(alpha = 0.35f), Color.Transparent),
            start = Offset.Zero,
            end = Offset(size.width * 0.5f, size.height * 0.5f),
        ),
    )
}

@Composable
fun Modifier.glass(
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
    alpha: Float = 0.65f,
): Modifier = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(tint.copy(alpha = alpha), tint.copy(alpha = alpha * 0.75f)),
        ),
        shape = shape,
    )

@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.96f,
    idleScale: Float = 1f,
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else idleScale,
        animationSpec = tween(durationMillis = 140),
        label = "pressScale",
    )
    return this.scale(scale)
}
