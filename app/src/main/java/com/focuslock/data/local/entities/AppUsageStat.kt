package com.focuslock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage_stats")
data class AppUsageStat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val usageMinutes: Int,
    val date: String,            // "yyyy-MM-dd"
    val category: String = "Other"
)
