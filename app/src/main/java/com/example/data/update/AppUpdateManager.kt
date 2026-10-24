package com.example.data.update

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class AppUpdatePayload(
    val title: String = "",
    val description: String = "",
    val targetVersionCode: Long = 0L,
    val targetVersionName: String = "",
    val downloadUrl: String = "",
    val isForceUpdate: Boolean = false,
    val isActive: Boolean = false
)

class AppUpdateManager(private val context: Context) {

    fun observeAppUpdates(): Flow<AppUpdatePayload?> = callbackFlow {
        val db = Firebase.firestore
        val docRef = db.collection("app_updates").document("latest")

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AppUpdateManager", "Listen failed: ${error.message}")
                trySend(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val isActive = snapshot.getBoolean("isActive") ?: false
                val targetCode = snapshot.getLong("targetVersionCode") ?: 0L
                val currentCode = BuildConfig.VERSION_CODE.toLong()

                if (isActive && targetCode > currentCode) {
                    val update = AppUpdatePayload(
                        title = snapshot.getString("title") ?: "নতুন আপডেট উপলব্ধ!",
                        description = snapshot.getString("description") ?: "অ্যাপটির নতুন এবং উন্নত সংস্করণ ইনস্টল করুন।",
                        targetVersionCode = targetCode,
                        targetVersionName = snapshot.getString("targetVersionName") ?: "",
                        downloadUrl = snapshot.getString("downloadUrl") ?: "",
                        isForceUpdate = snapshot.getBoolean("isForceUpdate") ?: false,
                        isActive = true
                    )
                    trySend(update)
                    return@addSnapshotListener
                }
            }
            trySend(null)
        }

        awaitClose { listener.remove() }
    }
}
