package com.focuslock.ui.screens.blockwebsites

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.focuslock.data.local.entities.BlockedWebsite
import com.focuslock.ui.theme.*
import com.focuslock.viewmodel.MainViewModel

@Composable
fun BlockWebsitesScreen(
    navController: NavController,
    vm: MainViewModel = hiltViewModel()
) {
    val sites by vm.blockedSites.collectAsStateWithLifecycle(emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var newDomain     by remember { mutableStateOf("") }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor   = CardDark,
            title = { Text("Add Website", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value  = newDomain,
                    onValueChange = { newDomain = it },
                    placeholder = { Text("e.g. youtube.com", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Purple400,
                        unfocusedBorderColor = DividerColor,
                        cursorColor          = Purple400,
                        focusedTextColor     = TextPrimary,
                        unfocusedTextColor   = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newDomain.isNotBlank()) {
                        vm.addWebsite(newDomain.trim())
                        newDomain = ""
                    }
                    showAddDialog = false
                }) { Text("Add", color = Purple400) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
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
            Text("Block Websites", style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f))
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, null, tint = TextPrimary)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sites, key = { it.domain }) { site ->
                WebsiteRow(site = site, onRemove = { vm.removeWebsite(site) })
            }

            // Info banner
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDark)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Shield, null,
                        tint = Purple400, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Websites will be blocked in supported browsers.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ChevronRight, null,
                        tint = TextMuted, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun WebsiteRow(site: BlockedWebsite, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Language, contentDescription = null,
            tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(
            text  = site.domain,
            fontSize = 15.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        // Remove (minus) button matching screenshot
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFF3A3A5C), CircleShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Remove, null, tint = TextMuted,
                modifier = Modifier.size(16.dp))
        }
    }
}
