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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
        .drawBehind {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(neu.highlight.copy(alpha = 0.35f), Color.Transparent),
                    start = Offset.Zero,
                    end = Offset(size.width * 0.5f, size.height * 0.5f),
                ),
            )
        }
        .background(color = surface, shape = shape)
}

/** Frosted-glass bar modifier for top bars and bottom bars. */
@Composable
fun Modifier.glassBar(isDark: Boolean): Modifier {
    val base        = if (isDark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.75f)
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.20f)
    val sheenAlpha  = if (isDark) 0.14f else 0.30f
    val borderAlpha = if (isDark) 0.20f else 0.35f

    return this
        .shadow(8.dp, ambientColor = shadowColor, spotColor = shadowColor)
        .drawBehind {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        base.copy(alpha = (base.alpha + 0.08f).coerceAtMost(1f)),
                        base,
                    ),
                ),
            )
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = sheenAlpha), Color.Transparent),
                    startY = 0f, endY = size.height * 0.48f,
                ),
            )
            // Bottom border line
            drawRect(
                color   = Color.White.copy(alpha = borderAlpha),
                topLeft = Offset(0f, size.height - 1f),
                size    = Size(size.width, 1f),
            )
        }
}

@Composable
fun Modifier.pressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.96f,
    idleScale: Float = 1f,
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue  = if (isPressed) pressedScale else idleScale,
        animationSpec = tween(durationMillis = 140),
        label        = "pressScale",
    )
    return this.scale(scale)
}
