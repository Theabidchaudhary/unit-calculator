package com.orwyx.unitcalculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A single line to plot: raw y-values, colour, optional dashing and gradient fill. */
data class LineSeries(
    val points: List<Float>,
    val color: Color,
    val filled: Boolean = false,
    val dashed: Boolean = false,
)

/**
 * A minimal, axis-free multi-line chart. Lines animate in left-to-right on first composition.
 * All series share one [maxValue] scale so they can be compared directly.
 */
@Composable
fun LineChart(
    series: List<LineSeries>,
    maxValue: Float,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
) {
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "lineReveal",
    )
    val safeMax = if (maxValue <= 0f) 1f else maxValue

    Canvas(modifier.fillMaxWidth().height(height)) {
        val w = size.width
        val h = size.height
        series.forEach { line ->
            if (line.points.size < 2) return@forEach
            val stepX = w / (line.points.size - 1)
            val path = Path()
            line.points.forEachIndexed { i, value ->
                val x = stepX * i
                val y = h - (value / safeMax).coerceIn(0f, 1f) * h
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            if (line.filled) {
                val fill = Path().apply {
                    addPath(path)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                clipRevealed(progress, w) {
                    drawPath(
                        fill,
                        brush = Brush.verticalGradient(
                            listOf(line.color.copy(alpha = 0.30f), Color.Transparent),
                        ),
                    )
                }
            }
            clipRevealed(progress, w) {
                drawPath(
                    path,
                    color = line.color,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = if (line.dashed)
                            PathEffect.dashPathEffect(floatArrayOf(14f, 12f)) else null,
                    ),
                )
            }
        }
    }
}

/** Clips drawing to a left-to-right reveal fraction. */
private inline fun androidx.compose.ui.graphics.drawscope.DrawScope.clipRevealed(
    progress: Float,
    fullWidth: Float,
    block: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit,
) {
    clipRect(right = (fullWidth * progress).coerceAtLeast(0.01f)) { block() }
}

/** Simple animated bar chart for daily usage. */
@Composable
fun BarChart(
    values: List<Float>,
    maxValue: Float,
    barColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 140.dp,
) {
    val anim by animateFloatAsState(targetValue = 1f, animationSpec = tween(900), label = "bars")
    val safeMax = if (maxValue <= 0f) 1f else maxValue
    Canvas(modifier.fillMaxWidth().height(height)) {
        if (values.isEmpty()) return@Canvas
        val gap = 4.dp.toPx()
        val barWidth = (size.width - gap * (values.size - 1)) / values.size
        values.forEachIndexed { i, v ->
            val barHeight = (v / safeMax).coerceIn(0f, 1f) * size.height * anim
            val left = i * (barWidth + gap)
            drawRoundRect(
                color = barColor,
                topLeft = Offset(left, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 3f, barWidth / 3f),
            )
        }
    }
}
