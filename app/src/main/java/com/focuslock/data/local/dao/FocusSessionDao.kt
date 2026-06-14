package com.focuslock.data.local.dao

import androidx.room.*
import com.focuslock.data.local.entities.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert
    suspend fun insert(session: FocusSession): Long

    @Update
    suspend fun update(session: FocusSession)

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE date = :date")
    fun getSessionsForDate(date: String): Flow<List<FocusSession>>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE date = :date AND isCompleted = 1")
    suspend fun getCompletedSessionsCount(date: String): Int

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE date = :date AND isCompleted = 1")
    suspend fun getTotalFocusMinutes(date: String): Int?

    @Query("SELECT * FROM focus_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY startTime ASC")
    fun getSessionsInRange(startDate: String, endDate: String): Flow<List<FocusSession>>
}
