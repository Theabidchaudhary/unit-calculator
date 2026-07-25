package com.orwyx.unitcalculator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import com.orwyx.unitcalculator.core.util.Formatters

/** A number that smoothly counts up/down to its target value when it changes. */
@Composable
fun AnimatedCounterText(
    value: Double,
    modifier: Modifier = Modifier,
    suffix: String = "",
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
) {
    val animated by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(durationMillis = 700),
        label = "counter",
    )
    Text(
        text = Formatters.units(animated.toDouble()) + suffix,
        modifier = modifier,
        style = style,
        color = color,
    )
}
