package com.example.data.telemetry

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class DeviceTelemetryManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("mixtools_device_telemetry", Context.MODE_PRIVATE)

    fun getDeviceId(): String {
        var id = prefs.getString("unique_device_id", null)
        if (id.isNullOrBlank()) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString("unique_device_id", id).apply()
        }
        return id
    }

    suspend fun registerDeviceTelemetry() = withContext(Dispatchers.IO) {
        try {
            val deviceId = getDeviceId()
            val manufacturer = Build.MANUFACTURER.orEmpty().replaceFirstChar { it.uppercase() }
            val model = Build.MODEL.orEmpty()
            val deviceName = if (model.startsWith(manufacturer, ignoreCase = true)) {
                model
            } else {
                "$manufacturer $model"
            }

            val firstSeen = prefs.getLong("first_seen_millis", 0L).let {
                if (it == 0L) {
                    val now = System.currentTimeMillis()
                    prefs.edit().putLong("first_seen_millis", now).apply()
                    now
                } else it
            }

            val telemetryData = hashMapOf(
                "deviceId" to deviceId,
                "modelName" to deviceName,
                "brand" to manufacturer,
                "modelCode" to model,
                "androidVersion" to "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                "sdkLevel" to Build.VERSION.SDK_INT,
                "appVersionName" to BuildConfig.VERSION_NAME,
                "appVersionCode" to BuildConfig.VERSION_CODE,
                "firstInstalledMillis" to firstSeen,
                "lastActiveMillis" to System.currentTimeMillis(),
                "deviceLocale" to context.resources.configuration.locales[0].toLanguageTag()
            )

            val db = Firebase.firestore
            db.collection("devices").document(deviceId)
                .set(telemetryData, SetOptions.merge())
            Log.d("DeviceTelemetry", "Device registered successfully: $deviceName")
        } catch (e: Exception) {
            Log.w("DeviceTelemetry", "Failed to sync device telemetry: ${e.message}")
        }
    }
}
