package com.focuslock.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.focuslock.MainActivity
import com.focuslock.data.repository.FocusRepository
import com.focuslock.utils.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class FocusMonitorService : Service() {

    @Inject lateinit var repository: FocusRepository
    @Inject lateinit var prefs: PreferenceManager

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val CHANNEL_ID = "focuslock_channel"
    private val NOTIF_ID   = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification("Focus session active"))
        monitorFocusEnd()
    }

    private fun monitorFocusEnd() {
        scope.launch {
            while (isActive) {
                val endTime = prefs.focusEndTime.first()
                if (endTime > 0 && System.currentTimeMillis() >= endTime) {
                    prefs.setFocusActive(false)
                    stopSelf()
                    break
                }
                delay(10_000L)
            }
        }
    }

    private fun buildNotification(text: String): Notification {
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle("FocusLock Active")
            .setContentText(text)
            .setContentIntent(pi)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        NotificationChannel(CHANNEL_ID, "Focus Session", NotificationManager.IMPORTANCE_LOW)
            .apply { description = "Shows while a focus session is active" }
            .also { getSystemService(NotificationManager::class.java).createNotificationChannel(it) }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
