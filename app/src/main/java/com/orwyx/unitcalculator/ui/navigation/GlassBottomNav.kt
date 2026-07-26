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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val primary = MaterialTheme.colorScheme.primary
    val isDark  = LocalNeuColors.current.isDark
    val tabs    = BottomTab.entries
    val selectedIndex = tabs.indexOfFirst { it.route == currentRoute }.coerceIn(0, tabs.lastIndex)

    val barGlass    = if (isDark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.62f)
    val barSheen    = if (isDark) 0.10f else 0.28f
    val borderAlpha = if (isDark) 0.18f else 0.68f
    val shadowColor = Color.Black.copy(alpha = if (isDark) 0.50f else 0.18f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(
                elevation     = 20.dp,
                shape         = MaterialTheme.shapes.extraLarge,
                ambientColor  = shadowColor,
                spotColor     = primary.copy(alpha = 0.28f),
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .drawBehind {
                val cr = CornerRadius(50.dp.toPx())
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            barGlass.copy(alpha = (barGlass.alpha + 0.10f).coerceAtMost(1f)),
                            barGlass,
                        ),
                    ),
                    cornerRadius = cr,
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = barSheen), Color.Transparent),
                        startY = 0f, endY = size.height * 0.5f,
                    ),
                    cornerRadius = cr,
                )
                drawRoundRect(
                    color        = Color.White.copy(alpha = borderAlpha),
                    cornerRadius = cr,
                    style        = Stroke(width = 1.dp.toPx()),
                )
            }
            .padding(8.dp)
            .height(56.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        val slotWidth = maxWidth / tabs.size
        val indicatorOffset by animateDpAsState(
            targetValue   = slotWidth * selectedIndex,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
            label         = "navIndicatorOffset",
        )

        // Selected pill with top-to-bottom accent gradient
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(slotWidth)
                .fillMaxHeight()
                .padding(4.dp)
                .shadow(10.dp, CircleShape, ambientColor = primary.copy(alpha = 0.45f), spotColor = primary.copy(alpha = 0.55f))
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primary.copy(alpha = 0.82f), primary),
                    ),
                ),
        )

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                NavTab(
                    tab      = tab,
                    selected = currentRoute == tab.route,
                    onClick  = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavTab(tab: BottomTab, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val content by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tabContent",
    )
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .pressScale(interactionSource, pressedScale = 0.92f)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Icon(tab.icon, contentDescription = tab.label, tint = content, modifier = Modifier.size(22.dp))
        if (selected) {
            Spacer(Modifier.size(8.dp))
            Text(tab.label, color = content, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}
