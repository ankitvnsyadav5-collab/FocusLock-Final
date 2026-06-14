package com.focuslock.data.local.dao

import androidx.room.*
import com.focuslock.data.local.entities.BlockedWebsite
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedWebsiteDao {
    @Query("SELECT * FROM blocked_websites ORDER BY domain ASC")
    fun getAllWebsites(): Flow<List<BlockedWebsite>>

    @Query("SELECT * FROM blocked_websites WHERE isBlocked = 1")
    fun getBlockedWebsites(): Flow<List<BlockedWebsite>>

    @Query("SELECT domain FROM blocked_websites WHERE isBlocked = 1")
    suspend fun getBlockedDomains(): List<String>

    @Upsert
    suspend fun upsert(site: BlockedWebsite)

    @Delete
    suspend fun delete(site: BlockedWebsite)
}
