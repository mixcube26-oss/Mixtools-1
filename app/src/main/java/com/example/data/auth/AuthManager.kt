package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("mixtools_auth_prefs", Context.MODE_PRIVATE)
    private val auth: FirebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(
        auth.currentUser?.let { AuthState.Authenticated(it) } ?: AuthState.Idle
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
            } else if (_authState.value is AuthState.Authenticated) {
                _authState.value = AuthState.Idle
            }
        }
    }

    fun getWebClientId(): String? {
        // 1. Try to read from generated R.string.default_web_client_id
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) {
            val generatedId = runCatching { context.getString(resId) }.getOrNull()
            if (!generatedId.isNullOrBlank()) {
                return generatedId
            }
        }

        // 2. Try to read from stored preferences if user configured a client ID
        val customClientId = prefs.getString("custom_web_client_id", null)
        if (!customClientId.isNullOrBlank()) {
            return customClientId
        }

        return null
    }

    fun saveCustomWebClientId(clientId: String) {
        prefs.edit().putString("custom_web_client_id", clientId.trim()).apply()
    }

    suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser> = withContext(Dispatchers.Main) {
        _authState.value = AuthState.Loading

        val clientId = getWebClientId()
        if (clientId.isNullOrBlank()) {
            val errorMsg = "Web Client ID পাওয়া যায়নি। Firebase Console-এ Google Sign-in চালু করে SHA-1 যোগ করুন।"
            _authState.value = AuthState.Error(errorMsg)
            return@withContext Result.failure(IllegalStateException(errorMsg))
        }

        return@withContext try {
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: throw IllegalStateException("Firebase user was null after sign in")

                _currentUser.value = user
                _authState.value = AuthState.Authenticated(user)
                Result.success(user)
            } else {
                val msg = "অপ্রত্যাশিত ক্রেডেনশিয়াল প্রাপ্ত হয়েছে"
                _authState.value = AuthState.Error(msg)
                Result.failure(IllegalStateException(msg))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("AuthManager", "Google Sign-In flow cancelled: ${e.message}")
            _authState.value = _currentUser.value?.let { AuthState.Authenticated(it) } ?: AuthState.Idle
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthManager", "Google Sign-In failed", e)
            val errorMsg = e.localizedMessage ?: "Google Sign-In ব্যর্থ হয়েছে"
            _authState.value = AuthState.Error(errorMsg)
            Result.failure(e)
        }
    }

    suspend fun signOut(activity: Activity? = null): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _currentUser.value = null
            _authState.value = AuthState.Idle
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthManager", "Sign out failed", e)
            Result.failure(e)
        }
    }

    fun clearError() {
        _authState.value = _currentUser.value?.let { AuthState.Authenticated(it) } ?: AuthState.Idle
    }
}
