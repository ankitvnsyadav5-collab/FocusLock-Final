package com.focuslock.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK

class ScheduleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("action") ?: return
        val serviceIntent = Intent(context, FocusMonitorService::class.java)
        if (action == "start") {
            context.startForegroundService(serviceIntent)
        } else {
            context.stopService(serviceIntent)
        }
    }
}
