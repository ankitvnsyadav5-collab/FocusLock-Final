package com.focuslock.ui.screens.blockscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.focuslock.ui.theme.*

class BlockScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val blockedPackage = intent.getStringExtra("blocked_package") ?: ""
        setContent {
            FocusLockTheme {
                BlockScreen(
                    blockedPackage = blockedPackage,
                    onGoBack       = { finish() }
                )
            }
        }
    }

    // Prevent back navigation when strict mode is on (simplified)
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // In strict mode, do nothing — user can only tap "Go Back"
        super.onBackPressed()
    }
}

@Composable
fun BlockScreen(
    blockedPackage: String,
    onGoBack: () -> Unit
) {
    // Pulsing glow on the lock icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 80f, targetValue = 130f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowRadius"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val quotes = listOf(
        "\"Discipline is choosing between what you want now and what you want most.\"",
        "\"The secret of getting ahead is getting started.\"",
        "\"Focus on being productive instead of busy.\"",
        "\"Small steps every day lead to big results.\""
    )
    val quote = remember { quotes.random() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0A18), Color(0xFF110830), Color(0xFF0A0A18))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        // ── Glowing lock circle ──
        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(scale)
                .drawBehind {
                    drawCircle(
                        color  = Purple500.copy(alpha = glowAlpha),
                        radius = glowRadius.dp.toPx() / 2
                    )
                }
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF3D1A8A), Color(0xFF1A0850))
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(Purple400, PurpleLight)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint   = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // ── App name ──
        Text(
            text = "FocusLock",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Purple400,
            letterSpacing = 1.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "This app is blocked",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Spacer(Modifier.height(8.dp))

        // Small underline accent
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(listOf(Purple400, PurpleLight))
                )
        )

        Spacer(Modifier.height(32.dp))

        // ── Motivational quote ──
        Text(
            text = quote,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(Modifier.weight(1f))

        // ── Go Back button ──
        Box(
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
                )
                .clickable(onClick = onGoBack)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Go Back",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
