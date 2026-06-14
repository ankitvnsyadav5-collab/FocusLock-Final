package com.focuslock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val dailyLimitMinutes: Int = 0,       // 0 = no limit
    val usedMinutesToday: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
