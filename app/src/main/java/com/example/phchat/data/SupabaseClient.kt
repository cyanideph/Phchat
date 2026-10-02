package com.example.phchat.data

import android.util.Log
import com.example.phchat.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object SupabaseConfig {
    private const val TAG = "SupabaseConfig"

    val url: String = try {
        BuildConfig.SUPABASE_URL.ifBlank { "https://mauhdrdnlrvjkekxencu.supabase.co" }
    } catch (e: Throwable) {
        "https://mauhdrdnlrvjkekxencu.supabase.co"
    }

    val publishableKey: String = try {
        BuildConfig.SUPABASE_KEY
    } catch (e: Throwable) {
        ""
    }

    val isConfigured: Boolean
        get() = publishableKey.isNotBlank() && !publishableKey.contains("placeholder")

    init {
        if (!isConfigured) {
            Log.w(TAG, "Supabase key is not configured or using placeholder.")
        } else {
            Log.i(TAG, "Supabase configured with endpoint: $url")
        }
    }
}

class SupabaseService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        if (!SupabaseConfig.isConfigured) {
            return@withContext Result.failure(IllegalStateException("Supabase key is missing"))
        }

        try {
            val request = Request.Builder()
                .url("${SupabaseConfig.url}/rest/v1/rooms?select=id,name,province_code&limit=5")
                .header("apikey", SupabaseConfig.publishableKey)
                .header("Authorization", "Bearer ${SupabaseConfig.publishableKey}")
                .header("Accept", "application/json")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                Result.success(body)
            } else {
                Result.failure(Exception("Supabase HTTP ${response.code}: $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postgrestGet(endpoint: String): Result<String> = withContext(Dispatchers.IO) {
        if (!SupabaseConfig.isConfigured) {
            return@withContext Result.failure(IllegalStateException("Supabase key is not configured"))
        }

        try {
            val url = if (endpoint.startsWith("http")) endpoint else "${SupabaseConfig.url}/rest/v1/$endpoint"
            val request = Request.Builder()
                .url(url)
                .header("apikey", SupabaseConfig.publishableKey)
                .header("Authorization", "Bearer ${SupabaseConfig.publishableKey}")
                .header("Accept", "application/json")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                Result.success(body)
            } else {
                Result.failure(Exception("Supabase HTTP ${response.code}: $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun callRpc(rpcName: String, jsonPayload: String = "{}"): Result<String> = withContext(Dispatchers.IO) {
        if (!SupabaseConfig.isConfigured) {
            return@withContext Result.failure(IllegalStateException("Supabase key is not configured"))
        }

        try {
            val request = Request.Builder()
                .url("${SupabaseConfig.url}/rest/v1/rpc/$rpcName")
                .header("apikey", SupabaseConfig.publishableKey)
                .header("Authorization", "Bearer ${SupabaseConfig.publishableKey}")
                .header("Content-Type", "application/json")
                .post(jsonPayload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                Result.success(body)
            } else {
                Result.failure(Exception("RPC error ${response.code}: $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
