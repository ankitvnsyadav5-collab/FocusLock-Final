package com.focuslock.ui.screens.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.focuslock.ui.navigation.NavRoutes
import com.focuslock.ui.theme.*
import com.focuslock.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    vm: MainViewModel = hiltViewModel()
) {
    val context     = LocalContext.current
    val strictMode by vm.strictMode.collectAsStateWithLifecycle(initialValue = false)
    val isPremium by vm.isPremium.collectAsStateWithLifecycle(initialValue = false)
    var showPinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
            }
            Text("Settings", style = MaterialTheme.typography.headlineSmall)
        }

        // ── Premium Banner ──
        if (!isPremium) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFF2D1060), Color(0xFF1A0A40))
                        )
                    )
                    .border(1.dp, PurpleDim, RoundedCornerShape(16.dp))
                    .clickable { navController.navigate(NavRoutes.PREMIUM) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑", fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Go Premium", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Unlock all features", fontSize = 12.sp, color = PurpleLight)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = PurpleLight)
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        SettingsSection(title = "Blocking") {
            SettingsNavRow(
                icon  = Icons.Default.Apps,
                label = "Block Apps",
                onClick = { navController.navigate(NavRoutes.BLOCK_APPS) }
            )
            SettingsDivider()
            SettingsNavRow(
                icon  = Icons.Default.Language,
                label = "Block Websites",
                onClick = { navController.navigate(NavRoutes.BLOCK_SITES) }
            )
            SettingsDivider()
            SettingsNavRow(
                icon  = Icons.Default.Schedule,
                label = "Schedule",
                onClick = { navController.navigate(NavRoutes.SCHEDULE) }
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSection(title = "Protection") {
            SettingsToggleRow(
                icon    = Icons.Default.Security,
                label   = "Strict Mode",
                subtext = "Prevents disabling blocks during focus",
                checked = strictMode,
                onToggle= { vm.setStrictMode(it) }
            )
            SettingsDivider()
            SettingsNavRow(
                icon  = Icons.Default.Lock,
                label = "PIN Protection",
                onClick = { showPinDialog = true }
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSection(title = "Permissions") {
            SettingsNavRow(
                icon  = Icons.Default.Accessibility,
                label = "Accessibility Service",
                badge = "Required",
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )
            SettingsDivider()
            SettingsNavRow(
                icon  = Icons.Default.QueryStats,
                label = "Usage Access",
                badge = "Required",
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )
            SettingsDivider()
            SettingsNavRow(
                icon  = Icons.Default.Layers,
                label = "Display Over Other Apps",
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSection(title = "Account") {
            SettingsNavRow(
                icon  = Icons.Default.CloudSync,
                label = "Firebase Cloud Sync",
                onClick = {}
            )
            SettingsDivider()
            SettingsNavRow(
                icon  = Icons.Default.Person,
                label = "Account",
                onClick = {}
            )
            SettingsDivider()
            SettingsNavRow(
                icon  = Icons.Default.Logout,
                label = "Sign Out",
                tint  = Color(0xFFFF5252),
                onClick = {}
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "FocusLock v1.0.0",
            fontSize = 12.sp,
            color = TextMuted,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = TextMuted, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark),
            content = content
        )
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = DividerColor, thickness = 0.5.dp
    )
}

@Composable
fun SettingsNavRow(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    tint: Color = Purple400,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 15.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFF5252).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(badge, fontSize = 10.sp, color = Color(0xFFFF5252),
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(8.dp))
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextMuted,
            modifier = Modifier.size(18.dp))
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    label: String,
    subtext: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Purple400.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Purple400, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 15.sp, color = TextPrimary)
            Text(subtext, fontSize = 11.sp, color = TextMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = Purple500,
                uncheckedThumbColor = Color(0xFF8888AA),
                uncheckedTrackColor = Color(0xFF2A2A40)
            )
        )
    }
}
