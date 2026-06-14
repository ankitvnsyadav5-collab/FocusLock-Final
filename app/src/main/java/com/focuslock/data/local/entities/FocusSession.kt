package com.focuslock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int,
    val blockedAppsCount: Int = 0,
    val blockedWebsitesCount: Int = 0,
    val isCompleted: Boolean = false,
    val date: String  // "yyyy-MM-dd"
)
