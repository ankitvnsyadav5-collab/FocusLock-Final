package com.focuslock.ui.screens.schedule

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.focuslock.data.local.entities.Schedule
import com.focuslock.ui.theme.*
import com.focuslock.viewmodel.MainViewModel

data class ScheduleUiItem(
    val id: Long = 0,
    val name: String,
    val days: String,
    val timeRange: String,
    val isEnabled: Boolean
)

@Composable
fun ScheduleScreen(
    navController: NavController,
    vm: MainViewModel = hiltViewModel()
) {
    val schedules by vm.schedules.collectAsStateWithLifecycle(emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

    // Default sample schedules matching screenshot
    val displaySchedules = if (schedules.isEmpty()) listOf(
        ScheduleUiItem(1, "Study Time",    "Mon - Fri", "5:00 PM - 10:00 PM", true),
        ScheduleUiItem(2, "Morning Focus", "Mon - Fri", "6:00 AM - 8:00 AM",  true),
        ScheduleUiItem(3, "Weekend Focus", "Sat - Sun", "9:00 AM - 1:00 PM",  false),
        ScheduleUiItem(4, "Exam Mode",     "Daily",     "24 Hours",            false)
    ) else schedules.map {
        ScheduleUiItem(it.id, it.name, parseDays(it.daysOfWeek),
            "${it.startTime} - ${it.endTime}", it.isEnabled)
    }

    if (showAddDialog) {
        AddScheduleDialog(
            onDismiss = { showAddDialog = false },
            onAdd     = { name, days, start, end ->
                vm.addSchedule(Schedule(
                    name = name, daysOfWeek = days,
                    startTime = start, endTime = end
                ))
                showAddDialog = false
            }
        )
    }

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
            Text("Schedule", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f))
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, null, tint = TextPrimary)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displaySchedules, key = { it.id }) { item ->
                ScheduleCard(
                    item     = item,
                    onToggle = { vm.toggleSchedule(item.id, !item.isEnabled) }
                )
            }
        }
    }
}

@Composable
fun ScheduleCard(item: ScheduleUiItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("${item.days} | ${item.timeRange}", fontSize = 12.sp, color = TextMuted)
        }
        Switch(
            checked = item.isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor  = Color.White,
                checkedTrackColor  = Purple500,
                uncheckedThumbColor= Color(0xFF8888AA),
                uncheckedTrackColor= Color(0xFF2A2A40)
            )
        )
    }
}

@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    var name  by remember { mutableStateOf("") }
    var days  by remember { mutableStateOf("1,2,3,4,5") }
    var start by remember { mutableStateOf("09:00") }
    var end   by remember { mutableStateOf("17:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor  = CardDark,
        title = { Text("New Schedule", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FocusTextField("Schedule Name", name) { name = it }
                FocusTextField("Start Time (HH:mm)", start) { start = it }
                FocusTextField("End Time (HH:mm)", end) { end = it }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onAdd(name, days, start, end)
            }) { Text("Add", color = Purple400, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        }
    )
}

@Composable
fun FocusTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp, color = TextMuted) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Purple400,
            unfocusedBorderColor = DividerColor,
            cursorColor          = Purple400,
            focusedTextColor     = TextPrimary,
            unfocusedTextColor   = TextPrimary,
            focusedLabelColor    = Purple400
        )
    )
}

fun parseDays(daysStr: String): String = when (daysStr) {
    "1,2,3,4,5"   -> "Mon - Fri"
    "6,7"          -> "Sat - Sun"
    "1,2,3,4,5,6,7"-> "Daily"
    else           -> daysStr
}
