package com.orwyx.unitcalculator.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.orwyx.unitcalculator.ui.screens.history.HistoryScreen
import com.orwyx.unitcalculator.ui.screens.meters.MeterDetailScreen
import com.orwyx.unitcalculator.ui.screens.meters.MeterEditScreen
import com.orwyx.unitcalculator.ui.screens.settings.SettingsScreen

@Composable
fun UnitCalculatorNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { slideInHorizontally(tween(360)) { it / 2 } + fadeIn(tween(360)) },
        exitTransition = { fadeOut(tween(220)) + slideOutHorizontally(tween(360)) { -it / 6 } },
        popEnterTransition = { fadeIn(tween(260)) + slideInHorizontally(tween(360)) { -it / 6 } },
        popExitTransition = { slideOutHorizontally(tween(360)) { it / 2 } + fadeOut(tween(360)) },
    ) {
        composable("home") {
            HomeScaffold(
                onOpenMeter = { id -> navController.navigate(Routes.meterDetail(id)) },
                onAddMeter = { navController.navigate(Routes.meterEdit()) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
        composable(route = Routes.METER_DETAIL, arguments = listOf(navArgument(Routes.ARG_METER_ID) { type = NavType.StringType })) {
            MeterDetailScreen(onBack = { navController.popBackStack() }, onEdit = { id -> navController.navigate(Routes.meterEdit(id)) })
        }
        composable(route = Routes.METER_EDIT, arguments = listOf(navArgument(Routes.ARG_METER_ID) { type = NavType.StringType; nullable = true; defaultValue = null })) {
            MeterEditScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() }, onOpenHistory = { navController.navigate(Routes.HISTORY) })
        }
        composable(Routes.HISTORY) { HistoryScreen(onBack = { navController.popBackStack() }) }
    }
}
