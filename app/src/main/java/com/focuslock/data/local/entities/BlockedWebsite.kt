package com.focuslock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_websites")
data class BlockedWebsite(
    @PrimaryKey val domain: String,
    val isBlocked: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
