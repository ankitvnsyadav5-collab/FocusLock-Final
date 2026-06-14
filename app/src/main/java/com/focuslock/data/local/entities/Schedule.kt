package com.focuslock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val daysOfWeek: String,    // "1,2,3,4,5" (1=Mon)
    val startTime: String,     // "HH:mm"
    val endTime: String,       // "HH:mm"
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
