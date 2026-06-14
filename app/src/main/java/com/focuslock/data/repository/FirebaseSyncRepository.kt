package com.focuslock.data.repository

import com.focuslock.data.local.entities.BlockedApp
import com.focuslock.data.local.entities.BlockedWebsite
import com.focuslock.data.local.entities.Schedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val localRepo: FocusRepository
) {
    private val uid get() = auth.currentUser?.uid

    // ── Auth ──
    suspend fun signInWithEmail(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    suspend fun createAccount(email: String, password: String): Result<Unit> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await()
        Unit
    }

    fun signOut() = auth.signOut()
    fun currentUser() = auth.currentUser
    fun isSignedIn() = auth.currentUser != null

    // ── Cloud Sync: Push local data to Firestore ──
    suspend fun syncToCloud(): Result<Unit> = runCatching {
        val id = uid ?: throw Exception("Not signed in")
        val userDoc = firestore.collection("users").document(id)

        // Blocked apps
        val apps = localRepo.getBlockedPackageNames()
        userDoc.collection("blockedApps")
            .document("list")
            .set(mapOf("packages" to apps))
            .await()

        // Blocked websites
        val domains = localRepo.getBlockedDomains()
        userDoc.collection("blockedWebsites")
            .document("list")
            .set(mapOf("domains" to domains))
            .await()
    }

    // ── Cloud Sync: Pull from Firestore ──
    suspend fun syncFromCloud(): Result<Unit> = runCatching {
        val id = uid ?: throw Exception("Not signed in")
        val userDoc = firestore.collection("users").document(id)

        // Pull blocked apps
        val appsSnap = userDoc.collection("blockedApps").document("list").get().await()
        @Suppress("UNCHECKED_CAST")
        val packages = appsSnap.get("packages") as? List<String> ?: emptyList()
        packages.forEach { pkg ->
            localRepo.upsertApp(BlockedApp(pkg, pkg, true))
        }

        // Pull blocked websites
        val sitesSnap = userDoc.collection("blockedWebsites").document("list").get().await()
        @Suppress("UNCHECKED_CAST")
        val domains = sitesSnap.get("domains") as? List<String> ?: emptyList()
        domains.forEach { domain ->
            localRepo.upsertWebsite(BlockedWebsite(domain, true))
        }
    }

    // ── Sync focus session stats ──
    suspend fun uploadSessionStats(date: String): Result<Unit> = runCatching {
        val id = uid ?: return@runCatching
        val minutes  = localRepo.getTotalFocusMinutes(date)
        val sessions = localRepo.getCompletedSessionsCount(date)

        firestore.collection("users").document(id)
            .collection("stats").document(date)
            .set(mapOf(
                "focusMinutes" to minutes,
                "sessions"     to sessions,
                "date"         to date
            )).await()
    }
}
