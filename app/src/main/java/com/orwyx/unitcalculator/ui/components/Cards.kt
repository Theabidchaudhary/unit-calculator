package com.orwyx.unitcalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.neumorphic

/** Standard raised neumorphic surface used for most cards in the app. */
@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    contentPadding: Dp = 18.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    var base = modifier.neumorphic(shape = shape, surface = MaterialTheme.colorScheme.surface)
    if (onClick != null) base = base.clickable(onClick = onClick)
    Box(base.padding(contentPadding)) { content() }
}
