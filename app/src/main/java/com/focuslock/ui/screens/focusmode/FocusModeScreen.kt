package com.focuslock.ui.screens.focusmode

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.focuslock.ui.components.GradientButton
import com.focuslock.ui.theme.*
import com.focuslock.viewmodel.MainViewModel

@Composable
fun FocusModeScreen(
    navController: NavController,
    vm: MainViewModel = hiltViewModel()
) {
    val isFocusActive by vm.isFocusActive.collectAsStateWithLifecycle(initialValue = false)
    val duration by vm.focusDuration.collectAsStateWithLifecycle(initialValue = 30)

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "glowAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        horizontalAlignment = Alignment.CenterHorizontally
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
            Text("Focus Mode", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // ── Illustration / starfield box ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1A0E3A), Color(0xFF0D0D1A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Stars
            repeat(12) { i ->
                val x = (i * 37 + 20) % 280
                val y = (i * 53 + 15) % 160
                Box(
                    modifier = Modifier
                        .offset(x.dp / 4, y.dp / 4)
                        .size(2.dp)
                        .background(Color.White.copy(alpha = 0.6f), CircleShape)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌙", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text("📚", fontSize = 28.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Stay focused. Achieve more.",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Block distracting apps & websites\nduring your focus session.",
            fontSize = 13.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // ── You are blocking card ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(20.dp)
        ) {
            Text("You are blocking", fontSize = 13.sp, color = TextMuted)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Apps, null,
                    tint = Purple400, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("6 Apps", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.width(24.dp))
                Box(modifier = Modifier.width(1.dp).height(18.dp).background(DividerColor))
                Spacer(Modifier.width(24.dp))
                Icon(Icons.Default.Language, null,
                    tint = Purple400, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("5 Websites", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Duration card ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CardDark)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Duration", fontSize = 13.sp, color = TextMuted)
                Text("$duration Minutes", fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            IconButton(onClick = { navController.navigate("set_duration") }) {
                Icon(Icons.Default.Edit, null, tint = Purple400,
                    modifier = Modifier.size(18.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        Box(modifier = Modifier.padding(20.dp)) {
            GradientButton(
                text = if (isFocusActive) "Stop Focus" else "Start Focus",
                onClick = {
                    if (isFocusActive) vm.stopFocus()
                    else vm.startFocus(duration)
                    navController.popBackStack()
                }
            )
        }
    }
}
