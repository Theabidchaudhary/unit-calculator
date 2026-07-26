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

/** Frosted-glass card surface used throughout the app. */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: Dp = 18.dp,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape  = RoundedCornerShape(cornerRadius)
    val isDark = LocalNeuColors.current.isDark

    // When a meter color tint is supplied, blend it into the glass base
    val glassBase = when {
        backgroundColor != null && isDark -> {
            val t = backgroundColor
            Color(
                red   = (0.09f + t.red   * t.alpha * 0.9f).coerceAtMost(1f),
                green = (0.09f + t.green * t.alpha * 0.9f).coerceAtMost(1f),
                blue  = (0.09f + t.blue  * t.alpha * 0.9f).coerceAtMost(1f),
                alpha = (0.14f + t.alpha * 0.9f).coerceAtMost(0.40f),
            )
        }
        backgroundColor != null -> {
            val t = backgroundColor
            Color(
                red   = (0.60f + t.red   * t.alpha * 0.8f).coerceAtMost(1f),
                green = (0.60f + t.green * t.alpha * 0.8f).coerceAtMost(1f),
                blue  = (0.60f + t.blue  * t.alpha * 0.8f).coerceAtMost(1f),
                alpha = (0.62f + t.alpha * 0.7f).coerceAtMost(0.90f),
            )
        }
        isDark -> Color.White.copy(alpha = 0.09f)
        else   -> Color.White.copy(alpha = 0.62f)
    }

    val borderAlpha = if (isDark) 0.18f else 0.72f
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.45f else 0.12f)
    val sheenAlpha  = if (isDark) 0.13f else 0.28f

    var base: Modifier = modifier
        .shadow(8.dp, shape, ambientColor = shadowColor, spotColor = shadowColor)
        .drawBehind {
            val cr = CornerRadius(cornerRadius.toPx())
            // Glass fill — subtle gradient top to bottom
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glassBase.copy(alpha = (glassBase.alpha + 0.10f).coerceAtMost(1f)),
                        glassBase,
                    ),
                ),
                cornerRadius = cr,
            )
            // Top-edge sheen (frosted highlight)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = sheenAlpha), Color.Transparent),
                    startY = 0f,
                    endY   = size.height * 0.42f,
                ),
                cornerRadius = cr,
            )
            // 1px inner border — the defining glass edge
            drawRoundRect(
                color        = Color.White.copy(alpha = borderAlpha),
                cornerRadius = cr,
                style        = Stroke(width = 1.dp.toPx()),
            )
        }

    if (onClick != null) base = base.clickable(onClick = onClick)
    Box(base.padding(contentPadding)) { content() }
}
