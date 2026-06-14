package com.focuslock.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.focuslock.data.repository.FocusRepository
import com.focuslock.ui.screens.blockscreen.BlockScreenActivity
import com.focuslock.utils.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class FocusAccessibilityService : AccessibilityService() {

    @Inject lateinit var repository: FocusRepository
    @Inject lateinit var prefs: PreferenceManager

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var blockedPackages = setOf<String>()
    private var isFocusActive = false
    private var lastBlockedPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Watch focus state and blocked packages
        scope.launch {
            prefs.isFocusActive.collect { active ->
                isFocusActive = active
                if (active) {
                    blockedPackages = repository.getBlockedPackageNames().toSet()
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (!isFocusActive) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == "com.focuslock") return
        if (packageName == lastBlockedPackage) return

        if (blockedPackages.contains(packageName)) {
            lastBlockedPackage = packageName
            showBlockScreen(packageName)
        } else {
            lastBlockedPackage = null
        }
    }

    private fun showBlockScreen(packageName: String) {
        val intent = Intent(this, BlockScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("blocked_package", packageName)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
