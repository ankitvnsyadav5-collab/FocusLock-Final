package com.focuslock.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.focuslock.ui.theme.*

@Composable
fun StatisticsScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Daily", "Weekly", "Monthly")

    val hourlyData = listOf(
        10f, 5f, 3f, 2f, 8f, 30f, 55f, 45f, 60f, 70f, 50f, 40f,
        35f, 45f, 55f, 65f, 80f, 90f, 75f, 60f, 40f, 25f, 15f, 8f
    )
    val maxVal = hourlyData.max()

    val categories = listOf(
        Triple("Productive",    0.66f, CategoryProductive),
        Triple("Social",        0.20f, CategorySocial),
        Triple("Entertainment", 0.14f, CategoryEntertain)
    )

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
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text(
                "Statistics",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
        }

        // Tab bar
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { i, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(9.dp))
                        .background(if (selectedTab == i) Purple500 else Color.Transparent)
                        .clickable { selectedTab = i }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedTab == i) Color.White else TextMuted
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Date navigator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextMuted)
            }
            Text("Today, 14 May", fontSize = 15.sp,
                fontWeight = FontWeight.Medium, color = TextPrimary)
            IconButton(onClick = {}) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Total time
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("3h 45m", fontSize = 48.sp, fontWeight = FontWeight.Bold,
                color = TextPrimary, letterSpacing = (-2).sp)
            Text("Total Screen Time", fontSize = 13.sp, color = TextMuted)
        }

        Spacer(Modifier.height(20.dp))

        // Bar Chart Card
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .padding(16.dp)
            ) {
                Column {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)) {
                        // Y-axis labels
                        Column(
                            modifier = Modifier
                                .width(32.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("60m", "90m", "30m", "0m").forEach { label ->
                                Text(label, fontSize = 9.sp, color = TextMuted,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth())
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Canvas(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()) {
                            drawHourlyBars(hourlyData, maxVal, size.width, size.height)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("12 AM", "6 AM", "12 PM", "6 PM", "12 AM").forEach { label ->
                            Text(label, fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Category breakdown
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Category", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(Modifier.height(12.dp))
            categories.forEach { (name, fraction, color) ->
                CategoryRow(name = name, fraction = fraction, color = color)
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

fun DrawScope.drawHourlyBars(data: List<Float>, maxVal: Float, w: Float, h: Float) {
    val count     = data.size
    val barWidth  = w / (count * 1.6f)
    val gap       = barWidth * 0.6f
    val totalSlot = barWidth + gap
    val cornerR   = barWidth / 2f

    data.forEachIndexed { i, value ->
        val barH    = (value / maxVal) * h * 0.9f
        val left    = i * totalSlot
        val top     = h - barH
        val isActive = i in 16..20

        drawRoundRect(
            color        = if (isActive) ChartBar1 else ChartBarDim,
            topLeft      = Offset(left, top),
            size         = Size(barWidth, barH),
            cornerRadius = CornerRadius(cornerR, cornerR)
        )
    }
}

@Composable
fun CategoryRow(name: String, fraction: Float, color: Color) {
    val icons = mapOf(
        "Productive"    to "💼",
        "Social"        to "👥",
        "Entertainment" to "🎮"
    )
    val labels = mapOf(
        "Productive"    to "2h 30m (66%)",
        "Social"        to "45m (20%)",
        "Entertainment" to "30m (14%)"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icons[name] ?: "📱", fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(labels[name] ?: "", fontSize = 12.sp, color = TextMuted)
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(CardDark2)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
            }
        }
    }
}
