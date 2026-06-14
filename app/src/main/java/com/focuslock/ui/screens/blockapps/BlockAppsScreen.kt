package com.focuslock.ui.screens.blockapps

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.focuslock.data.local.entities.BlockedApp
import com.focuslock.ui.components.*
import com.focuslock.ui.navigation.NavRoutes
import com.focuslock.ui.theme.*
import com.focuslock.utils.getInstalledUserApps
import com.focuslock.viewmodel.MainViewModel

@Composable
fun BlockAppsScreen(
    navController: NavController,
    vm: MainViewModel = hiltViewModel()
) {
    val context       = LocalContext.current
    val blockedApps   by vm.blockedApps.collectAsStateWithLifecycle(emptyList())
    var showBlocked   by remember { mutableStateOf(false) }
    var searchQuery   by remember { mutableStateOf("") }

    val installedApps = remember { context.getInstalledUserApps() }
    val blockedSet    = blockedApps.map { it.packageName }.toSet()

    val displayList = remember(installedApps, blockedApps, showBlocked, searchQuery) {
        val base = if (showBlocked) installedApps.filter { it.packageName in blockedSet }
                   else installedApps
        if (searchQuery.isBlank()) base
        else base.filter { it.appName.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
            }
            Text("Block Apps", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(Icons.Default.Search, null, tint = TextPrimary)
            }
        }

        // ── Toggle tabs: All Apps / Blocked ──
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .padding(4.dp)
        ) {
            TabButton("All Apps", !showBlocked) { showBlocked = false }
            TabButton("Blocked", showBlocked) { showBlocked = true }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(displayList, key = { it.packageName }) { appInfo ->
                val isBlocked = appInfo.packageName in blockedSet
                AppBlockRow(
                    appName    = appInfo.appName,
                    packageName= appInfo.packageName,
                    isBlocked  = isBlocked,
                    onToggle   = { vm.toggleAppBlock(appInfo.packageName, appInfo.appName, !isBlocked) }
                )
            }
        }

        // ── Next Button ──
        Box(modifier = Modifier.padding(20.dp)) {
            GradientButton(
                text = "Next",
                onClick = { navController.navigate(NavRoutes.SET_DURATION) }
            )
        }
    }
}

@Composable
fun RowScope.TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) Purple500 else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else TextMuted)
    }
}

@Composable
fun AppBlockRow(
    appName: String,
    packageName: String,
    isBlocked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App icon placeholder with colored background
        val color = appIconColor(packageName)
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(appName.first().uppercase(), fontSize = 18.sp,
                fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = appName,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        // Purple checkbox matching the screenshot
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isBlocked) Purple500 else CardDark2)
                .border(
                    1.dp,
                    if (isBlocked) Purple500 else Color(0xFF3A3A5C),
                    RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center
        ) {
            if (isBlocked) {
                Icon(Icons.Default.Check, null, tint = Color.White,
                    modifier = Modifier.size(16.dp))
            }
        }
    }
}

fun appIconColor(packageName: String): Color {
    val colors = listOf(
        Color(0xFFE91E63), Color(0xFFFF5722), Color(0xFF2196F3),
        Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF9C27B0),
        Color(0xFF00BCD4), Color(0xFFF44336)
    )
    return colors[packageName.hashCode().and(Int.MAX_VALUE) % colors.size]
}
