package com.focuslock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.focuslock.ui.components.FocusBottomNavBar
import com.focuslock.ui.navigation.FocusLockNavHost
import com.focuslock.ui.navigation.NavRoutes
import com.focuslock.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FocusLockTheme {
                FocusLockApp()
            }
        }
    }
}

@Composable
fun FocusLockApp() {
    val navController = rememberNavController()

    // Routes where bottom nav should be hidden
    val noNavRoutes = setOf(
        NavRoutes.BLOCK_SCREEN,
        NavRoutes.PIN,
        NavRoutes.SET_DURATION
    )

    val currentRoute by remember {
        derivedStateOf {
            navController.currentBackStackEntry?.destination?.route
        }
    }
    val showBottomNav = currentRoute !in noNavRoutes

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        containerColor = BackgroundDark,
        bottomBar = {
            if (showBottomNav) {
                FocusBottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FocusLockNavHost(navController = navController)
        }
    }
}
