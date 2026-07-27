package com.orwyx.unitcalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.neumorphic

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: Dp = 18.dp,
    backgroundColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val surface = backgroundColor ?: MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    var base = modifier.neumorphic(shape = shape, surface = surface)
    if (onClick != null) base = base.clickable(onClick = onClick)
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(base.padding(contentPadding)) { content() }
    }
}
