package com.focuslock.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false
)

fun Context.getInstalledUserApps(): List<AppInfo> {
    val pm = packageManager
    return pm.getInstalledApplications(PackageManager.GET_META_DATA)
        .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        .map { ai ->
            AppInfo(
                packageName = ai.packageName,
                appName     = pm.getApplicationLabel(ai).toString(),
                icon        = try { pm.getApplicationIcon(ai.packageName) } catch (e: Exception) { null },
                isSystemApp = false
            )
        }
        .sortedBy { it.appName }
}

fun formatMinutes(minutes: Int): String = when {
    minutes < 60 -> "${minutes}m"
    minutes % 60 == 0 -> "${minutes / 60}h"
    else -> "${minutes / 60}h ${minutes % 60}m"
}

fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11  -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        in 18..21 -> "Good Evening"
        else      -> "Good Night"
    }
}
