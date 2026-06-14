package com.focuslock.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(NavRoutes.DASHBOARD, "Home",
        Icons.Filled.Home, Icons.Outlined.Home)
    object Block : BottomNavItem(NavRoutes.BLOCK_APPS, "Block",
        Icons.Filled.Block, Icons.Outlined.Block)
    object Focus : BottomNavItem(NavRoutes.FOCUS_MODE, "Focus",
        Icons.Filled.Timer, Icons.Outlined.Timer)
    object Stats : BottomNavItem(NavRoutes.STATISTICS, "Stats",
        Icons.Filled.BarChart, Icons.Outlined.BarChart)
    object Settings : BottomNavItem(NavRoutes.SETTINGS, "Settings",
        Icons.Filled.Settings, Icons.Outlined.Settings)
}
