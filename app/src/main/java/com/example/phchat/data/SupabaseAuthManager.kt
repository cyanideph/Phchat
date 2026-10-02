package com.example.phchat.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AuthUser(
    val id: String,
    val email: String,
    val username: String = "",
    val displayName: String = "",
    val accessToken: String,
    val refreshToken: String = ""
)

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class SupabaseAuthManager(
    context: Context,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("phchat_supabase_auth", Context.MODE_PRIVATE)

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        val token = prefs.getString("access_token", null)
        val userId = prefs.getString("user_id", null)
        val email = prefs.getString("email", null)
        val username = prefs.getString("username", "") ?: ""
        val displayName = prefs.getString("display_name", "") ?: ""
        val refreshToken = prefs.getString("refresh_token", "") ?: ""

        if (!token.isNullOrBlank() && !userId.isNullOrBlank() && !email.isNullOrBlank()) {
            val user = AuthUser(
                id = userId,
                email = email,
                username = username,
                displayName = if (displayName.isNotBlank()) displayName else email.substringBefore("@"),
                accessToken = token,
                refreshToken = refreshToken
            )
            _authState.value = AuthState.Authenticated(user)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    suspend fun signIn(email: String, pass: String): Result<AuthUser> = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Loading
        try {
            val payload = JSONObject().apply {
                put("email", email.trim())
                put("password", pass)
            }.toString()

            val request = Request.Builder()
                .url("${SupabaseConfig.url}/auth/v1/token?grant_type=password")
                .header("apikey", SupabaseConfig.publishableKey)
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(body)
                    errJson.optString("error_description", errJson.optString("msg", "Sign-in failed (HTTP ${response.code})"))
                } catch (e: Exception) {
                    "Sign-in failed: ${response.code}"
                }
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }

            val json = JSONObject(body)
            val accessToken = json.getString("access_token")
            val refreshToken = json.optString("refresh_token", "")
            val userJson = json.getJSONObject("user")
            val userId = userJson.getString("id")
            val userEmail = userJson.getString("email")
            val meta = userJson.optJSONObject("user_metadata")
            val username = meta?.optString("username", "") ?: ""
            val displayName = meta?.optString("display_name", "") ?: userEmail.substringBefore("@")

            val user = AuthUser(
                id = userId,
                email = userEmail,
                username = username,
                displayName = displayName,
                accessToken = accessToken,
                refreshToken = refreshToken
            )

            saveSession(user)
            _authState.value = AuthState.Authenticated(user)
            Result.success(user)
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Network connection error"
            _authState.value = AuthState.Error(msg)
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, pass: String, username: String, displayName: String, province: String): Result<String> = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Loading
        try {
            val metadata = JSONObject().apply {
                put("username", username.trim())
                put("display_name", displayName.trim())
                put("province", province.trim())
            }

            val payload = JSONObject().apply {
                put("email", email.trim())
                put("password", pass)
                put("data", metadata)
            }.toString()

            val request = Request.Builder()
                .url("${SupabaseConfig.url}/auth/v1/signup")
                .header("apikey", SupabaseConfig.publishableKey)
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(body)
                    errJson.optString("msg", errJson.optString("error_description", "Sign-up failed"))
                } catch (e: Exception) {
                    "Sign-up failed: ${response.code}"
                }
                _authState.value = AuthState.Error(errorMsg)
                return@withContext Result.failure(Exception(errorMsg))
            }

            val json = JSONObject(body)
            val accessToken = json.optString("access_token", "")
            if (accessToken.isNotBlank()) {
                val userJson = json.getJSONObject("user")
                val user = AuthUser(
                    id = userJson.getString("id"),
                    email = userJson.getString("email"),
                    username = username,
                    displayName = displayName,
                    accessToken = accessToken,
                    refreshToken = json.optString("refresh_token", "")
                )
                saveSession(user)
                _authState.value = AuthState.Authenticated(user)
                Result.success("Account created and logged in!")
            } else {
                _authState.value = AuthState.Unauthenticated
                Result.success("Registration successful! Please check your email to confirm your account.")
            }
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "Failed to sign up"
            _authState.value = AuthState.Error(msg)
            Result.failure(e)
        }
    }

    fun signOut() {
        prefs.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }

    private fun saveSession(user: AuthUser) {
        prefs.edit()
            .putString("access_token", user.accessToken)
            .putString("refresh_token", user.refreshToken)
            .putString("user_id", user.id)
            .putString("email", user.email)
            .putString("username", user.username)
            .putString("display_name", user.displayName)
            .apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun getCurrentUserId(): String? {
        return prefs.getString("user_id", null)
    }
}
