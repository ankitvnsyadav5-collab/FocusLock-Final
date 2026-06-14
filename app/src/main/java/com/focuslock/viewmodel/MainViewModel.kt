package com.focuslock.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focuslock.data.local.entities.*
import com.focuslock.data.repository.FocusRepository
import com.focuslock.service.FocusMonitorService
import com.focuslock.utils.PreferenceManager
import com.focuslock.utils.formatMinutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val userName: String = "Aryan",
    val greeting: String = "Good Morning",
    val totalFocusMinutes: Int = 225,   // 3h 45m
    val focusPercent: Int = 78,
    val yesterdayPercent: Int = 66,
    val focusSessions: Int = 3,
    val blockedAppsCount: Int = 12,
    val blockedWebsitesCount: Int = 8,
    val topApps: List<AppUsageUiItem> = emptyList(),
    val isFocusActive: Boolean = false,
    val isPremium: Boolean = false
)

data class AppUsageUiItem(
    val packageName: String,
    val appName: String,
    val usageMinutes: Int,
    val percentage: Float = 0f,
    val colorIndex: Int = 0
)

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: FocusRepository,
    private val prefs: PreferenceManager
) : AndroidViewModel(application) {

    private val _dashboardState = MutableStateFlow(DashboardUiState())
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState.asStateFlow()

    val blockedApps    = repository.getBlockedApps()
    val allApps        = repository.getAllApps()
    val blockedSites   = repository.getBlockedWebsites()
    val allSites       = repository.getAllWebsites()
    val schedules      = repository.getAllSchedules()
    val isFocusActive  = prefs.isFocusActive
    val focusEndTime   = prefs.focusEndTime
    val focusDuration  = prefs.focusDurationMins
    val isPremium      = prefs.isPremium
    val strictMode     = prefs.strictMode
    val userName       = prefs.userName

    init { loadDashboard() }

    private fun loadDashboard() {
        viewModelScope.launch {
            val today = repository.todayString()
            val focusMins = repository.getTotalFocusMinutes(today)
            val sessions  = repository.getCompletedSessionsCount(today)
            val name      = prefs.userName.first()
            val premium   = prefs.isPremium.first()

            // Get top app usage from system
            val usageMap = repository.getRealUsageStats()
            val total    = usageMap.values.sum().coerceAtLeast(1)
            val topApps  = usageMap.entries
                .sortedByDescending { it.value }
                .take(4)
                .mapIndexed { i, (pkg, mins) ->
                    AppUsageUiItem(
                        packageName  = pkg,
                        appName      = getAppName(pkg),
                        usageMinutes = mins.toInt(),
                        percentage   = (mins.toFloat() / total * 100),
                        colorIndex   = i
                    )
                }

            _dashboardState.update {
                it.copy(
                    userName          = name,
                    greeting          = getGreeting(),
                    totalFocusMinutes = focusMins,
                    focusSessions     = sessions,
                    topApps           = topApps,
                    isPremium         = premium
                )
            }
        }
    }

    private fun getAppName(pkg: String): String {
        return try {
            val pm = getApplication<Application>().packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
        } catch (e: Exception) { pkg.substringAfterLast('.') }
    }

    private fun getGreeting(): String {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (h) { in 5..11 -> "Good Morning"; in 12..17 -> "Good Afternoon"; in 18..21 -> "Good Evening"; else -> "Good Night" }
    }

    // ── Focus control ──
    fun startFocus(durationMinutes: Int) {
        viewModelScope.launch {
            val endTime = System.currentTimeMillis() + durationMinutes * 60_000L
            prefs.setFocusActive(true, endTime, durationMinutes)
            val today = repository.todayString()
            val sessionId = repository.insertSession(
                FocusSession(startTime = System.currentTimeMillis(),
                    durationMinutes = durationMinutes, date = today)
            )
            prefs.setCurrentSessionId(sessionId)
            val intent = Intent(getApplication(), FocusMonitorService::class.java)
            getApplication<Application>().startForegroundService(intent)
        }
    }

    fun stopFocus() {
        viewModelScope.launch {
            prefs.setFocusActive(false)
            val intent = Intent(getApplication(), FocusMonitorService::class.java)
            getApplication<Application>().stopService(intent)
        }
    }

    // ── App blocking ──
    fun toggleAppBlock(packageName: String, appName: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.upsertApp(BlockedApp(packageName, appName, isBlocked))
        }
    }

    fun removeBlockedApp(app: BlockedApp) {
        viewModelScope.launch { repository.deleteApp(app) }
    }

    // ── Website blocking ──
    fun addWebsite(domain: String) {
        viewModelScope.launch {
            repository.upsertWebsite(BlockedWebsite(domain.lowercase().trim()))
        }
    }

    fun removeWebsite(site: BlockedWebsite) {
        viewModelScope.launch { repository.deleteWebsite(site) }
    }

    // ── Schedules ──
    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch { repository.insertSchedule(schedule) }
    }

    fun toggleSchedule(id: Long, enabled: Boolean) {
        viewModelScope.launch { repository.setScheduleEnabled(id, enabled) }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch { repository.deleteSchedule(schedule) }
    }

    // ── Settings ──
    fun setStrictMode(enabled: Boolean) {
        viewModelScope.launch { prefs.setStrictMode(enabled) }
    }

    fun setUserName(name: String) {
        viewModelScope.launch { prefs.setUserName(name) }
    }
}
