package com.focuslock.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.focuslock.ui.screens.blockapps.BlockAppsScreen
import com.focuslock.ui.screens.blockwebsites.BlockWebsitesScreen
import com.focuslock.ui.screens.dashboard.DashboardScreen
import com.focuslock.ui.screens.focusmode.FocusModeScreen
import com.focuslock.ui.screens.focusmode.SetDurationScreen
import com.focuslock.ui.screens.premium.PremiumScreen
import com.focuslock.ui.screens.schedule.ScheduleScreen
import com.focuslock.ui.screens.settings.SettingsScreen
import com.focuslock.ui.screens.statistics.StatisticsScreen

@Composable
fun FocusLockNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavRoutes.DASHBOARD) {
        composable(NavRoutes.DASHBOARD)   { DashboardScreen(navController) }
        composable(NavRoutes.BLOCK_APPS)  { BlockAppsScreen(navController) }
        composable(NavRoutes.BLOCK_SITES) { BlockWebsitesScreen(navController) }
        composable(NavRoutes.FOCUS_MODE)  { FocusModeScreen(navController) }
        composable(NavRoutes.SET_DURATION){ SetDurationScreen(navController) }
        composable(NavRoutes.SCHEDULE)    { ScheduleScreen(navController) }
        composable(NavRoutes.STATISTICS)  { StatisticsScreen(navController) }
        composable(NavRoutes.SETTINGS)    { SettingsScreen(navController) }
        composable(NavRoutes.PREMIUM)     { PremiumScreen(navController) }
    }
}
