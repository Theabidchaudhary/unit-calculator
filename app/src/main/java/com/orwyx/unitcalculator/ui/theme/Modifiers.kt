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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.neumorphic(
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    elevation: Dp = 6.dp,
    surface: Color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
): Modifier {
    val neu = LocalNeuColors.current
    // Light mode: soft black shadows on white for clean Material depth
    // Dark mode: dark navy shadows for layered depth
    val ambientAlpha = if (neu.isDark) 0.70f else 0.07f
    val spotAlpha    = if (neu.isDark) 0.50f else 0.16f
    return this
        .shadow(
            elevation    = elevation,
            shape        = shape,
            ambientColor = neu.shadow.copy(alpha = ambientAlpha),
            spotColor    = neu.shadow.copy(alpha = spotAlpha),
        )
        .background(color = surface, shape = shape)
}

/**
 * Frosted-glass bar: blurred background layer only (content stays sharp when
 * placed as a sibling composable using matchParentSize or Box ordering).
 */
@Composable
fun Modifier.frostedBlurBackground(isDark: Boolean): Modifier {
    val baseColor = if (isDark) Color.Black.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.72f)
    return this
        .blur(radius = 24.dp)
        .background(baseColor)
}

/** Frosted-glass top-border line + shadow overlay (not blurred, drawn on top of blur layer). */
@Composable
fun Modifier.glassBar(isDark: Boolean): Modifier {
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.50f else 0.18f)
    val borderAlpha = if (isDark) 0.22f else 0.45f
    return this
        .shadow(8.dp, ambientColor = shadowColor, spotColor = shadowColor)
        .drawBehind {
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
        targetValue   = if (isPressed) pressedScale else idleScale,
        animationSpec = tween(durationMillis = 140),
        label         = "pressScale",
    )
    return this.scale(scale)
}
