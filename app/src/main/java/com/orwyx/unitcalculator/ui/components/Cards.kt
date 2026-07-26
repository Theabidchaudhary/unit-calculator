package com.orwyx.unitcalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors
import com.orwyx.unitcalculator.ui.theme.WarmAccent

/** Strong frosted-glass card. [backgroundColor] is ignored — always pure glass. */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: Dp = 18.dp,
    backgroundColor: Color? = null,  // kept for API compat; not used
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape  = RoundedCornerShape(cornerRadius)
    val isDark = LocalNeuColors.current.isDark

    // Stronger glass — clearly frosted but still see-through
    val glassBase   = if (isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.22f)
    val sheenAlpha  = if (isDark) 0.20f else 0.35f
    val borderAlpha = if (isDark) 0.30f else 0.55f
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.60f else 0.25f)

    var base: Modifier = modifier
        .shadow(12.dp, shape, ambientColor = shadowColor, spotColor = shadowColor)
        .drawBehind {
            val cr = CornerRadius(cornerRadius.toPx())
            // Glass fill
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassBase.copy(alpha = (glassBase.alpha + 0.08f).coerceAtMost(1f)),
                        glassBase,
                    ),
                ),
                cornerRadius = cr,
            )
            // Top-edge frosted sheen
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = sheenAlpha), Color.Transparent),
                    startY = 0f,
                    endY   = size.height * 0.45f,
                ),
                cornerRadius = cr,
            )
            // Crisp 1.5px glass border
            drawRoundRect(
                color        = Color.White.copy(alpha = borderAlpha),
                cornerRadius = cr,
                style        = Stroke(width = 1.5.dp.toPx()),
            )
        }

    if (onClick != null) base = base.clickable(onClick = onClick)
    Box(base.padding(contentPadding)) { content() }
}

/**
 * Applies an accent-gradient overlay (bottom→top, fading) on top of a solid base.
 * Use on buttons and date cells: solid base color in the caller + this modifier on top.
 */
fun Modifier.accentGradientOverlay(
    accent: Color = WarmAccent,
    cornerRadius: Dp = 12.dp,
): Modifier {
    return this.drawBehind {
        val cr = CornerRadius(cornerRadius.toPx())
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    accent.copy(alpha = 0.40f),
                ),
                startY = 0f,
                endY   = size.height,
            ),
            cornerRadius = cr,
        )
    }
}
