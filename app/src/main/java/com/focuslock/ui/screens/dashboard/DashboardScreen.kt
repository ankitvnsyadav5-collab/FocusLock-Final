package com.focuslock.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.focuslock.ui.components.*
import com.focuslock.ui.navigation.NavRoutes
import com.focuslock.ui.theme.*
import com.focuslock.utils.formatMinutes
import com.focuslock.viewmodel.AppUsageUiItem
import com.focuslock.viewmodel.MainViewModel
import kotlin.math.min

@Composable
fun DashboardScreen(
    navController: NavController,
    vm: MainViewModel = hiltViewModel()
) {
    val state by vm.dashboardState.collectAsStateWithLifecycle()
    val isFocusActive by vm.isFocusActive.collectAsStateWithLifecycle(initialValue = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu",
                tint = TextSecondary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Text("Focus", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextPrimary)
            Text("Lock", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Purple400)
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFB300)),
                contentAlignment = Alignment.Center
            ) { Text("👑", fontSize = 18.sp) }
        }

        // Greeting
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "${state.greeting}, ${state.userName}! 👋",
                fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary
            )
            Text(
                text = "Stay focused, achieve more.",
                fontSize = 13.sp, color = TextMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Start Focus Button
        GradientButton(
            text    = if (isFocusActive) "⏹  Stop Focus" else "▶  Start Focus",
            onClick = {
                if (isFocusActive) vm.stopFocus()
                else navController.navigate(NavRoutes.FOCUS_MODE)
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Today's Summary card
        FocusCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Today's Summary", fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp, color = TextSecondary)
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${state.totalFocusMinutes / 60}h ${state.totalFocusMinutes % 60}m",
                        fontWeight = FontWeight.Bold, fontSize = 36.sp,
                        color = TextPrimary, letterSpacing = (-1).sp
                    )
                    Text("Total Focus Time", fontSize = 12.sp, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null,
                            tint = GreenAccent, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "↑ ${state.focusPercent - state.yesterdayPercent}% from yesterday",
                            fontSize = 12.sp, color = GreenAccent
                        )
                    }
                }
                FocusProgressRing(percent = state.focusPercent)
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(state.blockedAppsCount.toString(), "Blocked Apps")
                VerticalDivider()
                StatItem(state.blockedWebsitesCount.toString(), "Blocked Websites")
                VerticalDivider()
                StatItem(state.focusSessions.toString(), "Focus Sessions")
            }
        }

        Spacer(Modifier.height(20.dp))

        // Most Used Apps header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Most Used Apps", fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp, color = TextPrimary)
            Text("View All", fontSize = 13.sp, color = Purple400,
                modifier = Modifier.clickable { navController.navigate(NavRoutes.STATISTICS) })
        }

        Spacer(Modifier.height(12.dp))

        FocusCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            val sampleApps = listOf(
                AppUsageUiItem("com.google.android.youtube",    "YouTube",   80, 60f, 0),
                AppUsageUiItem("com.instagram.android",         "Instagram", 45, 35f, 1),
                AppUsageUiItem("com.android.chrome",            "Chrome",    30, 24f, 2),
                AppUsageUiItem("com.whatsapp",                  "WhatsApp",  25, 19f, 3)
            )
            val displayApps = if (state.topApps.isEmpty()) sampleApps else state.topApps
            displayApps.forEach { app ->
                AppUsageRow(app = app, modifier = Modifier.padding(vertical = 6.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun FocusProgressRing(percent: Int, size: Dp = 90.dp) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        val sweepAngle = 360f * percent / 100f
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val inset  = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)

            // Track
            drawArc(color = CardDark2, startAngle = -90f, sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = topLeft, size = arcSize)
            // Progress
            drawArc(
                brush = Brush.sweepGradient(listOf(Purple600, Purple400)),
                startAngle = -90f, sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = topLeft, size = arcSize
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text("Focused", fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
fun AppUsageRow(app: AppUsageUiItem, modifier: Modifier = Modifier) {
    val barColors = listOf(
        Color(0xFF7C4DFF), Color(0xFF9C6DFF),
        Color(0xFF5C9DFF), Color(0xFF5CDF9F)
    )
    val color = barColors[app.colorIndex % barColors.size]

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF252540)),
            contentAlignment = Alignment.Center
        ) {
            Text(app.appName.first().toString(), fontSize = 16.sp, color = TextPrimary)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(app.appName, fontSize = 13.sp,
                    fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(formatMinutes(app.usageMinutes), fontSize = 12.sp, color = TextMuted)
            }
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(CardDark2)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(min(app.percentage / 100f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
            }
        }
    }
}
