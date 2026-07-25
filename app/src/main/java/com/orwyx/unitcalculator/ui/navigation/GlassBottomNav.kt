package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.LocalNeuColors
import com.orwyx.unitcalculator.ui.theme.pressScale

@Composable
fun GlassBottomNav(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    val neu = LocalNeuColors.current
    val sheen = lerp(surface, surfaceVariant, 0.45f)
    val glossyBrush = Brush.verticalGradient(
        0.0f to sheen.copy(alpha = 0.98f),
        0.55f to surface.copy(alpha = 0.98f),
        1.0f to surface.copy(alpha = 1.0f),
    )
    val tabs = BottomTab.entries
    val selectedIndex = tabs.indexOfFirst { it.route == currentRoute }.coerceIn(0, tabs.lastIndex)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .shadow(elevation = 12.dp, shape = MaterialTheme.shapes.extraLarge, ambientColor = neu.shadow.copy(alpha = 0.45f), spotColor = neu.shadow.copy(alpha = 0.55f))
            .clip(MaterialTheme.shapes.extraLarge)
            .background(glossyBrush, shape = MaterialTheme.shapes.extraLarge)
            .drawBehind {
                drawRect(brush = Brush.verticalGradient(colors = listOf(Color.White.copy(alpha = if (neu.isDark) 0.06f else 0.18f), Color.Transparent), startY = 0f, endY = size.height * 0.45f))
                val border = onSurface.copy(alpha = if (neu.isDark) 0.10f else 0.06f)
                drawRect(color = border, topLeft = Offset(0f, 0f), size = Size(size.width, 1f))
                drawRect(color = border, topLeft = Offset(0f, size.height - 1f), size = Size(size.width, 1f))
                drawRect(color = border, topLeft = Offset(0f, 0f), size = Size(1f, size.height))
                drawRect(color = border, topLeft = Offset(size.width - 1f, 0f), size = Size(1f, size.height))
            }
            .padding(8.dp)
            .height(56.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        val slotWidth = maxWidth / tabs.size
        val indicatorOffset by animateDpAsState(
            targetValue = slotWidth * selectedIndex,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
            label = "navIndicatorOffset",
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(slotWidth)
                .fillMaxHeight()
                .padding(4.dp)
                .clip(CircleShape)
                .background(primary),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                NavTab(tab = tab, selected = currentRoute == tab.route, onClick = { onTabSelected(tab) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NavTab(tab: BottomTab, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val content by animateColorAsState(if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, label = "tabContent")
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .pressScale(interactionSource, pressedScale = 0.92f)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(tab.icon, contentDescription = tab.label, tint = content, modifier = Modifier.size(22.dp))
        if (selected) {
            Spacer(Modifier.size(8.dp))
            Text(tab.label, color = content, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun lerp(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(red = a.red + (b.red - a.red) * tt, green = a.green + (b.green - a.green) * tt, blue = a.blue + (b.blue - a.blue) * tt, alpha = a.alpha + (b.alpha - a.alpha) * tt)
}
