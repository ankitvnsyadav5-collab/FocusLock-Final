package com.focuslock.ui.screens.focusmode

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.focuslock.ui.components.GradientButton
import com.focuslock.ui.navigation.NavRoutes
import com.focuslock.ui.theme.*

@Composable
fun SetDurationScreen(navController: NavController) {
    var selectedOption by remember { mutableStateOf(4) } // "Custom" pre-selected

    val options = listOf(
        "1 Hour" to Icons.Default.Schedule,
        "1 Day"  to Icons.Default.CalendarMonth,
        "7 Days" to Icons.Default.DateRange,
        "30 Days"to Icons.Default.CalendarToday,
        "Custom Date & Time" to Icons.Default.EditCalendar
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
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
            Text("Set Duration", style = MaterialTheme.typography.headlineSmall)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { i, (label, icon) ->
                DurationOptionRow(
                    icon    = icon,
                    label   = label,
                    selected= selectedOption == i,
                    onClick = { selectedOption = i }
                )
            }

            // "Until" section
            if (selectedOption == 4) {
                Spacer(Modifier.height(8.dp))
                Text("Until", fontSize = 13.sp, color = TextMuted)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDark)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, null,
                        tint = Purple400, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("20 May 2024", fontSize = 15.sp,
                            fontWeight = FontWeight.Medium, color = TextPrimary)
                        Text("10:30 PM", fontSize = 13.sp, color = TextMuted)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = TextMuted)
                }
            }
        }

        Box(modifier = Modifier.padding(20.dp)) {
            GradientButton(
                text = "Confirm",
                onClick = { navController.navigate(NavRoutes.FOCUS_MODE) }
            )
        }
    }
}

@Composable
fun DurationOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, fontSize = 15.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        if (selected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Purple500),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White,
                    modifier = Modifier.size(14.dp))
            }
        } else {
            Icon(Icons.Default.ChevronRight, null, tint = TextMuted,
                modifier = Modifier.size(20.dp))
        }
    }
}
