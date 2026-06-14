package com.focuslock.data.local.dao

import androidx.room.*
import com.focuslock.data.local.entities.BlockedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {
    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    fun getAllApps(): Flow<List<BlockedApp>>

    @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1")
    fun getBlockedApps(): Flow<List<BlockedApp>>

    @Query("SELECT packageName FROM blocked_apps WHERE isBlocked = 1")
    suspend fun getBlockedPackageNames(): List<String>

    @Upsert
    suspend fun upsert(app: BlockedApp)

    @Delete
    suspend fun delete(app: BlockedApp)

    @Query("UPDATE blocked_apps SET isBlocked = :isBlocked WHERE packageName = :packageName")
    suspend fun setBlocked(packageName: String, isBlocked: Boolean)

    @Query("UPDATE blocked_apps SET usedMinutesToday = :minutes WHERE packageName = :packageName")
    suspend fun updateUsage(packageName: String, minutes: Int)

    @Query("UPDATE blocked_apps SET usedMinutesToday = 0")
    suspend fun resetDailyUsage()
}
