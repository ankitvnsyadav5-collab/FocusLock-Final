package com.focuslock.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Re-schedule any active schedules after reboot
        // (full implementation would query Room and reschedule alarms)
    }
}
