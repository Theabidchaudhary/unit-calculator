package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.theme.pressScale

private val SmoothEasing = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
private val NavShape = RoundedCornerShape(28.dp)

@Composable
fun GlassBottomNav(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary       = MaterialTheme.colorScheme.primary
    val tabs          = BottomTab.entries
    val selectedIndex = tabs.indexOfFirst { it.route == currentRoute }.coerceIn(0, tabs.lastIndex)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .height(72.dp)
            .shadow(
                elevation    = 12.dp,
                shape        = NavShape,
                ambientColor = Color.Black,
                spotColor    = Color.Black,
            )
            .background(MaterialTheme.colorScheme.surface, NavShape)
            .clip(NavShape),
        contentAlignment = Alignment.CenterStart,
    ) {
            val slotWidth = maxWidth / tabs.size
            val indicatorOffset by animateDpAsState(
                targetValue   = slotWidth * selectedIndex,
                animationSpec = tween(durationMillis = 420, easing = SmoothEasing),
                label         = "navIndicatorOffset",
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(slotWidth)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .shadow(8.dp, CircleShape, ambientColor = primary.copy(alpha = 0.35f), spotColor = primary.copy(alpha = 0.45f))
                    .clip(CircleShape)
                    .background(primary),
            )

            Row(
                modifier              = Modifier.fillMaxSize().padding(horizontal = 4.dp),
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
    val contentColor by animateColorAsState(
        targetValue   = if (selected) MaterialTheme.colorScheme.onPrimary
                        else          MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(280, easing = SmoothEasing),
        label         = "tabContent",
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
        Icon(tab.icon, contentDescription = tab.label, tint = contentColor, modifier = Modifier.size(22.dp))

        AnimatedVisibility(
            visible = selected,
            enter   = fadeIn(tween(280, easing = SmoothEasing)) +
                      expandHorizontally(tween(300, easing = SmoothEasing), expandFrom = Alignment.Start),
            exit    = fadeOut(tween(200, easing = SmoothEasing)) +
                      shrinkHorizontally(tween(240, easing = SmoothEasing), shrinkTowards = Alignment.Start),
        ) {
            Row {
                Spacer(Modifier.width(8.dp))
                Text(
                    tab.label,
                    color      = contentColor,
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
