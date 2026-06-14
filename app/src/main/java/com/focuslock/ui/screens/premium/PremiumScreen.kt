package com.focuslock.ui.screens.premium

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.focuslock.ui.theme.*

data class PremiumFeature(
    val icon: String,
    val title: String,
    val subtitle: String
)

@Composable
fun PremiumScreen(navController: NavController) {
    val features = listOf(
        PremiumFeature("🔒", "Unlimited App & Website Blocking", "Block as many as you want"),
        PremiumFeature("📅", "Advanced Scheduling",              "Custom schedules & auto modes"),
        PremiumFeature("📊", "Detailed Insights",               "Advanced analytics & reports"),
        PremiumFeature("🛡️", "Strict Mode",                     "Extra protection for your focus"),
        PremiumFeature("☁️", "Cloud Backup",                    "Sync your data across devices"),
        PremiumFeature("💬", "Priority Support",                "Get help when you need it")
    )

    var selectedPlan by remember { mutableStateOf(1) } // 0=monthly, 1=yearly

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Hero section ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF160840), Color(0xFF0D0D1A))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {

                // Crown icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF3D1A8A), Color(0xFF1A0850))
                            )
                        )
                        .border(2.dp,
                            Brush.linearGradient(listOf(Purple400, Color(0xFFFFB300))),
                            CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑", fontSize = 36.sp)
                }

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Text("Go Premium", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                        color = TextPrimary)
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFB300))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Best Value", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A0A00))
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Unlock all premium features and\ntake your focus to the next level.",
                    fontSize = 14.sp, color = TextSecondary,
                    textAlign = TextAlign.Center, lineHeight = 20.sp
                )
            }
        }

        // ── Feature grid ──
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            features.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { feature ->
                        FeatureCard(feature = feature, modifier = Modifier.weight(1f))
                    }
                    // Fill if odd number
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // ── Pricing card ──
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2D1060), Color(0xFF180840))
                    )
                )
                .border(1.dp,
                    Brush.linearGradient(listOf(Purple400, Color(0xFF5B21E8))),
                    RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            // Plan toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFFFFF))
                    .padding(4.dp)
            ) {
                listOf("Monthly", "Yearly").forEachIndexed { i, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                if (selectedPlan == i) Purple500
                                else Color.Transparent
                            )
                            .clickable { selectedPlan = i }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                            color = if (selectedPlan == i) Color.White else TextMuted)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (selectedPlan == 0) "₹299" else "₹1,499",
                    fontSize = 40.sp, fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary, letterSpacing = (-1).sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (selectedPlan == 0) "/ month" else "/ year",
                    fontSize = 16.sp, color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            if (selectedPlan == 1) {
                Text("Save 58% vs monthly · ₹125/month",
                    fontSize = 13.sp, color = GreenAccent)
            }

            Spacer(Modifier.height(20.dp))

            // CTA button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
                    )
                    .clickable {}
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Start Free Trial", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(12.dp))
            Text("3-Day Free Trial  ·  Cancel Anytime",
                fontSize = 12.sp, color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun FeatureCard(feature: PremiumFeature, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .padding(14.dp)
    ) {
        Text(feature.icon, fontSize = 24.sp)
        Spacer(Modifier.height(8.dp))
        Text(feature.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            color = TextPrimary, lineHeight = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text(feature.subtitle, fontSize = 11.sp, color = TextMuted, lineHeight = 15.sp)
    }
}
