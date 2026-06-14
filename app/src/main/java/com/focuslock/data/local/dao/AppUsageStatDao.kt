package com.focuslock.data.local.dao

import androidx.room.*
import com.focuslock.data.local.entities.AppUsageStat
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageStatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stat: AppUsageStat)

    @Query("SELECT * FROM app_usage_stats WHERE date = :date ORDER BY usageMinutes DESC")
    fun getStatsForDate(date: String): Flow<List<AppUsageStat>>

    @Query("SELECT * FROM app_usage_stats WHERE date BETWEEN :start AND :end ORDER BY usageMinutes DESC")
    fun getStatsInRange(start: String, end: String): Flow<List<AppUsageStat>>

    @Query("DELETE FROM app_usage_stats WHERE date < :cutoffDate")
    suspend fun deleteOldStats(cutoffDate: String)
}
