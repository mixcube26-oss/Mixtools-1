package com.example.firebase

/**
 * Clean architectural interfaces prepared for future Firebase integration
 * without requiring hardcoded secrets or mock responses during Phase 1.
 */

data class FirebaseUserSession(
    val userId: String,
    val email: String?,
    val displayName: String?,
    val isAnonymous: Boolean
)

interface FirebaseAuthGateway {
    fun getCurrentUser(): FirebaseUserSession?
    fun isUserSignedIn(): Boolean
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUserSession>
    suspend fun signOut()
}

interface FirestoreSyncGateway {
    suspend fun syncFavorites(userId: String, favoriteIds: List<String>): Result<Unit>
    suspend fun getRemoteFavorites(userId: String): Result<List<String>>
    suspend fun syncSettings(userId: String, preferencesJson: String): Result<Unit>
}

interface FirebaseStorageGateway {
    suspend fun uploadDocumentBackup(userId: String, documentId: String, data: ByteArray): Result<String>
    suspend fun downloadDocumentBackup(userId: String, documentId: String): Result<ByteArray>
}

interface RemoteConfigGateway {
    fun isFeatureEnabled(featureKey: String, defaultValue: Boolean = false): Boolean
    fun getStringParameter(key: String, defaultValue: String = ""): String
}

interface AnalyticsGateway {
    fun logToolLaunched(toolId: String, category: String)
    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap())
}

interface CrashReportingGateway {
    fun recordException(throwable: Throwable)
    fun logBreadcrumb(message: String)
}

/**
 * Local offline implementations for Phase 1 that allow seamless operation
 * without mandatory cloud dependencies.
 */
class LocalOfflineAuthGateway : FirebaseAuthGateway {
    override fun getCurrentUser(): FirebaseUserSession? = null
    override fun isUserSignedIn(): Boolean = false
    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUserSession> {
        return Result.failure(UnsupportedOperationException("Firebase Auth will be configured in cloud phase"))
    }
    override suspend fun signOut() {}
}

class LocalOfflineAnalyticsGateway : AnalyticsGateway {
    override fun logToolLaunched(toolId: String, category: String) {
        // Ready for Firebase Analytics bundle logging
    }
    override fun logEvent(eventName: String, parameters: Map<String, Any>) {
        // Ready for Firebase Analytics
    }
}
