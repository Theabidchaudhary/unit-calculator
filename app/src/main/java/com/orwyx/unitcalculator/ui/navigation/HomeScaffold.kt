package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.components.AddMeterFab
import com.orwyx.unitcalculator.ui.screens.meters.MetersScreen
import com.orwyx.unitcalculator.ui.screens.planning.PlanningScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScaffold(
    onOpenMeter: (Long) -> Unit,
    onAddMeter: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.METERS) }
    val pagerState  = rememberPagerState(initialPage = if (selectedTab == BottomTab.METERS) 0 else 1, pageCount = { 2 })
    val scope       = rememberCoroutineScope()
    val hazeState   = remember { HazeState() }

    // Padding so each screen's list doesn't hide under the floating bars
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()
    val navBarPadding    = WindowInsets.navigationBars.asPaddingValues()
    val contentPadding   = PaddingValues(
        top    = statusBarPadding.calculateTopPadding() + 64.dp,
        bottom = navBarPadding.calculateBottomPadding(),
    )

    LaunchedEffect(pagerState.currentPage) {
        val target = if (pagerState.currentPage == 0) BottomTab.METERS else BottomTab.PLANNING
        if (target != selectedTab) selectedTab = target
    }

    fun selectTab(tab: BottomTab) {
        selectedTab = tab
        scope.launch { pagerState.animateScrollToPage(if (tab == BottomTab.METERS) 0 else 1) }
    }

    Box(Modifier.fillMaxSize()) {
        // Full-screen pager is the haze source — bars blur whatever scrolls behind them
        HorizontalPager(
            state       = pagerState,
            modifier    = Modifier.fillMaxSize().haze(hazeState),
            pageSpacing = 0.dp,
        ) { page ->
            when (page) {
                0    -> MetersScreen(onOpenMeter = onOpenMeter, contentPadding = contentPadding)
                1    -> PlanningScreen(contentPadding = contentPadding)
                else -> Unit
            }
        }

        AppTopBar(
            title      = if (selectedTab == BottomTab.METERS) "Unit Calculator" else "Planning",
            hazeState  = hazeState,
            onSettings = onOpenSettings,
            modifier   = Modifier.align(Alignment.TopCenter),
        )

        if (selectedTab == BottomTab.METERS) {
            AddMeterFab(
                onClick  = onAddMeter,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 28.dp, bottom = 100.dp),
            )
        }

        GlassBottomNav(
            currentRoute  = selectedTab.route,
            hazeState     = hazeState,
            onTabSelected = ::selectTab,
            modifier      = Modifier.align(Alignment.BottomCenter),
        )
    }
}
