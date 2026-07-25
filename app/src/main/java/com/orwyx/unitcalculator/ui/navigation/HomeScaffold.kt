package com.orwyx.unitcalculator.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orwyx.unitcalculator.ui.screens.meters.MetersScreen
import com.orwyx.unitcalculator.ui.screens.planning.PlanningScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScaffold(
    onOpenMeter: (Long) -> Unit,
    onAddMeter: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenColorPicker: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.METERS) }
    val pagerState = rememberPagerState(initialPage = if (selectedTab == BottomTab.METERS) 0 else 1, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        val target = if (pagerState.currentPage == 0) BottomTab.METERS else BottomTab.PLANNING
        if (target != selectedTab) selectedTab = target
    }

    fun selectTab(tab: BottomTab) {
        selectedTab = tab
        scope.launch { pagerState.animateScrollToPage(if (tab == BottomTab.METERS) 0 else 1) }
    }

    // Back on planning tab → go to meters tab instead of exiting
    BackHandler(enabled = pagerState.currentPage == 1) {
        selectTab(BottomTab.METERS)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = if (selectedTab == BottomTab.METERS) "Unit Calculator" else "Planning", onSettings = onOpenSettings) },
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize(), pageSpacing = 0.dp) { page ->
                when (page) {
                    0 -> MetersScreen(onOpenMeter = onOpenMeter, onAddMeter = onAddMeter, contentPadding = padding)
                    1 -> PlanningScreen(
                        contentPadding = padding,
                        onOpenColorPicker = onOpenColorPicker,
                        onNavigateBack = { selectTab(BottomTab.METERS) },
                    )
                }
            }
            GlassBottomNav(currentRoute = selectedTab.route, onTabSelected = ::selectTab, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}
