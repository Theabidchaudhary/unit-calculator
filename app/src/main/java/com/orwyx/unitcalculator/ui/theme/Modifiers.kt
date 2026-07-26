package com.orwyx.unitcalculator.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Frosted-glass surface modifier — replaces the old neumorphic style.
 * [surface] is accepted for API compatibility; pass it as a meter color tint or
 * leave it as the default surface to get the plain glass look.
 */
@Composable
fun Modifier.neumorphic(
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    elevation: Dp = 8.dp,
    surface: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
): Modifier {
    val isDark    = LocalNeuColors.current.isDark
    // Use supplied surface only if it has a non-trivial alpha (i.e. a meter color tint)
    val glassBase = if (surface.alpha in 0.01f..0.99f) surface
                   else if (isDark) Color.White.copy(alpha = 0.09f)
                   else Color.White.copy(alpha = 0.60f)

    val borderAlpha = if (isDark) 0.18f else 0.70f
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.12f)
    val sheenAlpha  = if (isDark) 0.12f else 0.28f
    val cornerDp    = 24.dp  // matches the default; shape corner is resolved in DrawScope

    return this
        .shadow(elevation, shape, ambientColor = shadowColor, spotColor = shadowColor)
        .drawBehind {
            val cr = CornerRadius(cornerDp.toPx())
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassBase.copy(alpha = (glassBase.alpha + 0.10f).coerceAtMost(1f)),
                        glassBase,
                    ),
                ),
                cornerRadius = cr,
            )
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = sheenAlpha), Color.Transparent),
                    startY = 0f, endY = size.height * 0.42f,
                ),
                cornerRadius = cr,
            )
            drawRoundRect(
                color        = Color.White.copy(alpha = borderAlpha),
                cornerRadius = cr,
                style        = Stroke(width = 1.dp.toPx()),
            )
        }
}

/** Frosted-glass bar modifier for top bars and bottom bars. */
@Composable
fun Modifier.glassBar(isDark: Boolean): Modifier {
    val base        = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.62f)
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.40f else 0.12f)
    val sheenAlpha  = if (isDark) 0.10f else 0.26f
    val borderAlpha = if (isDark) 0.16f else 0.62f

    return this
        .shadow(10.dp, ambientColor = shadowColor, spotColor = shadowColor)
        .drawBehind {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        base.copy(alpha = (base.alpha + 0.10f).coerceAtMost(1f)),
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
                color     = Color.White.copy(alpha = borderAlpha),
                topLeft   = Offset(0f, size.height - 1f),
                size      = Size(size.width, 1f),
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
