package com.focuslock.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.focuslock.data.local.dao.*
import com.focuslock.data.local.entities.*

@Database(
    entities = [
        BlockedApp::class,
        BlockedWebsite::class,
        FocusSession::class,
        Schedule::class,
        AppUsageStat::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FocusLockDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun blockedWebsiteDao(): BlockedWebsiteDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun appUsageStatDao(): AppUsageStatDao
}
