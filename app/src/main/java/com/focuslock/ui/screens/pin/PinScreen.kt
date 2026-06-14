package com.focuslock.ui.screens.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.focuslock.ui.theme.*

@Composable
fun PinScreen(
    title: String = "Enter PIN",
    subtitle: String = "Enter your 4-digit PIN to continue",
    onPinEntered: (String) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        Text("🔒", fontSize = 48.sp)
        Spacer(Modifier.height(20.dp))

        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, fontSize = 14.sp, color = TextMuted, textAlign = TextAlign.Center)

        Spacer(Modifier.height(40.dp))

        // ── PIN dots ──
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isError && pin.length > i -> Color(0xFFFF5252)
                                pin.length > i            -> Purple400
                                else                      -> Color(0xFF2A2A45)
                            }
                        )
                        .border(
                            1.5.dp,
                            if (pin.length == i) Purple400 else Color.Transparent,
                            CircleShape
                        )
                )
            }
        }

        if (isError) {
            Spacer(Modifier.height(12.dp))
            Text("Incorrect PIN", fontSize = 13.sp, color = Color(0xFFFF5252))
        }

        Spacer(Modifier.height(48.dp))

        // ── Keypad ──
        val keys = listOf(
            listOf("1","2","3"),
            listOf("4","5","6"),
            listOf("7","8","9"),
            listOf("","0","⌫")
        )

        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                row.forEach { key ->
                    PinKey(
                        key = key,
                        onClick = {
                            if (key == "⌫") {
                                if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                isError = false
                            } else if (key.isNotEmpty() && pin.length < 4) {
                                pin += key
                                isError = false
                                if (pin.length == 4) {
                                    onPinEntered(pin)
                                    // Reset after callback
                                    pin = ""
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        if (onCancel != null) {
            TextButton(onClick = onCancel, modifier = Modifier.padding(bottom = 32.dp)) {
                Text("Cancel", color = TextMuted, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun PinKey(key: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                if (key.isEmpty()) Color.Transparent
                else Color(0xFF1E1E35)
            )
            .then(
                if (key.isNotEmpty())
                    Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (key == "⌫") {
            Icon(Icons.Default.Backspace, null, tint = TextSecondary,
                modifier = Modifier.size(22.dp))
        } else if (key.isNotEmpty()) {
            Text(key, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}
