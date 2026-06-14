package com.focuslock.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import com.focuslock.data.local.dao.*
import com.focuslock.data.local.entities.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val blockedAppDao: BlockedAppDao,
    private val blockedWebsiteDao: BlockedWebsiteDao,
    private val focusSessionDao: FocusSessionDao,
    private val scheduleDao: ScheduleDao,
    private val appUsageStatDao: AppUsageStatDao
) {
    // ── Apps ──
    fun getAllApps(): Flow<List<BlockedApp>> = blockedAppDao.getAllApps()
    fun getBlockedApps(): Flow<List<BlockedApp>> = blockedAppDao.getBlockedApps()
    suspend fun getBlockedPackageNames() = blockedAppDao.getBlockedPackageNames()
    suspend fun upsertApp(app: BlockedApp) = blockedAppDao.upsert(app)
    suspend fun deleteApp(app: BlockedApp) = blockedAppDao.delete(app)
    suspend fun setAppBlocked(packageName: String, isBlocked: Boolean) =
        blockedAppDao.setBlocked(packageName, isBlocked)

    // ── Websites ──
    fun getAllWebsites(): Flow<List<BlockedWebsite>> = blockedWebsiteDao.getAllWebsites()
    fun getBlockedWebsites(): Flow<List<BlockedWebsite>> = blockedWebsiteDao.getBlockedWebsites()
    suspend fun getBlockedDomains() = blockedWebsiteDao.getBlockedDomains()
    suspend fun upsertWebsite(site: BlockedWebsite) = blockedWebsiteDao.upsert(site)
    suspend fun deleteWebsite(site: BlockedWebsite) = blockedWebsiteDao.delete(site)

    // ── Focus Sessions ──
    fun getAllSessions(): Flow<List<FocusSession>> = focusSessionDao.getAllSessions()
    fun getSessionsForDate(date: String): Flow<List<FocusSession>> =
        focusSessionDao.getSessionsForDate(date)
    suspend fun insertSession(session: FocusSession): Long = focusSessionDao.insert(session)
    suspend fun updateSession(session: FocusSession) = focusSessionDao.update(session)
    suspend fun getCompletedSessionsCount(date: String) =
        focusSessionDao.getCompletedSessionsCount(date)
    suspend fun getTotalFocusMinutes(date: String) =
        focusSessionDao.getTotalFocusMinutes(date) ?: 0
    fun getSessionsInRange(start: String, end: String) =
        focusSessionDao.getSessionsInRange(start, end)

    // ── Schedules ──
    fun getAllSchedules(): Flow<List<Schedule>> = scheduleDao.getAllSchedules()
    suspend fun insertSchedule(schedule: Schedule): Long = scheduleDao.insert(schedule)
    suspend fun updateSchedule(schedule: Schedule) = scheduleDao.update(schedule)
    suspend fun deleteSchedule(schedule: Schedule) = scheduleDao.delete(schedule)
    suspend fun setScheduleEnabled(id: Long, enabled: Boolean) =
        scheduleDao.setEnabled(id, enabled)

    // ── Usage Stats ──
    fun getStatsForDate(date: String): Flow<List<AppUsageStat>> =
        appUsageStatDao.getStatsForDate(date)
    fun getStatsInRange(start: String, end: String): Flow<List<AppUsageStat>> =
        appUsageStatDao.getStatsInRange(start, end)

    // ── System Usage Stats via UsageStatsManager ──
    fun getRealUsageStats(date: Calendar = Calendar.getInstance()): Map<String, Long> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val startOfDay = (date.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        val now = System.currentTimeMillis()
        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startOfDay, now
        )
        return stats
            .filter { it.totalTimeInForeground > 0 }
            .associate { it.packageName to (it.totalTimeInForeground / 60000) }
    }

    fun todayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
