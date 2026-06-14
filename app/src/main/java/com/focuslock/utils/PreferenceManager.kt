package com.focuslock.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
) {
    companion object {
        val IS_FOCUS_ACTIVE       = booleanPreferencesKey("is_focus_active")
        val FOCUS_END_TIME        = longPreferencesKey("focus_end_time")
        val FOCUS_DURATION_MINS   = intPreferencesKey("focus_duration_mins")
        val IS_PREMIUM            = booleanPreferencesKey("is_premium")
        val PIN_ENABLED           = booleanPreferencesKey("pin_enabled")
        val PIN_HASH              = stringPreferencesKey("pin_hash")
        val STRICT_MODE           = booleanPreferencesKey("strict_mode")
        val USER_NAME             = stringPreferencesKey("user_name")
        val CURRENT_SESSION_ID    = longPreferencesKey("current_session_id")
        val ACCESSIBILITY_SHOWN   = booleanPreferencesKey("accessibility_shown")
        val DAILY_GOAL_MINUTES    = intPreferencesKey("daily_goal_minutes")
    }

    val isFocusActive: Flow<Boolean>      = dataStore.data.map { it[IS_FOCUS_ACTIVE] ?: false }
    val focusEndTime: Flow<Long>          = dataStore.data.map { it[FOCUS_END_TIME] ?: 0L }
    val focusDurationMins: Flow<Int>      = dataStore.data.map { it[FOCUS_DURATION_MINS] ?: 30 }
    val isPremium: Flow<Boolean>          = dataStore.data.map { it[IS_PREMIUM] ?: false }
    val pinEnabled: Flow<Boolean>         = dataStore.data.map { it[PIN_ENABLED] ?: false }
    val pinHash: Flow<String>             = dataStore.data.map { it[PIN_HASH] ?: "" }
    val strictMode: Flow<Boolean>         = dataStore.data.map { it[STRICT_MODE] ?: false }
    val userName: Flow<String>            = dataStore.data.map { it[USER_NAME] ?: "User" }
    val dailyGoalMinutes: Flow<Int>       = dataStore.data.map { it[DAILY_GOAL_MINUTES] ?: 240 }

    suspend fun setFocusActive(active: Boolean, endTime: Long = 0L, durationMins: Int = 30) {
        dataStore.edit {
            it[IS_FOCUS_ACTIVE]     = active
            it[FOCUS_END_TIME]      = endTime
            it[FOCUS_DURATION_MINS] = durationMins
        }
    }

    suspend fun setPremium(isPremium: Boolean) =
        dataStore.edit { it[IS_PREMIUM] = isPremium }

    suspend fun setPin(enabled: Boolean, hash: String = "") =
        dataStore.edit { it[PIN_ENABLED] = enabled; it[PIN_HASH] = hash }

    suspend fun setStrictMode(enabled: Boolean) =
        dataStore.edit { it[STRICT_MODE] = enabled }

    suspend fun setUserName(name: String) =
        dataStore.edit { it[USER_NAME] = name }

    suspend fun setCurrentSessionId(id: Long) =
        dataStore.edit { it[CURRENT_SESSION_ID] = id }

    suspend fun setDailyGoal(minutes: Int) =
        dataStore.edit { it[DAILY_GOAL_MINUTES] = minutes }
}
