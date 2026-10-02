package com.example.phchat.data

import android.util.Log
import com.example.phchat.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseRepository(
    private val authManager: SupabaseAuthManager,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val tag = "SupabaseRepo"

    private fun getAuthToken(): String {
        return authManager.getAccessToken() ?: SupabaseConfig.publishableKey
    }

    private fun buildRequest(url: String): Request.Builder {
        val token = getAuthToken()
        return Request.Builder()
            .url(url)
            .header("apikey", SupabaseConfig.publishableKey)
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
    }

    suspend fun getRooms(): Result<List<Room>> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseConfig.url}/rest/v1/rooms?select=*&order=created_at.desc"
            val request = buildRequest(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to fetch rooms: ${response.code}"))
            }

            val array = JSONArray(body)
            val list = mutableListOf<Room>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val name = obj.optString("name", obj.optString("slug", "Room"))
                val slug = obj.optString("slug", id)
                val provinceCode = obj.optString("province_code", "NCR")
                val isLocked = obj.optBoolean("is_locked", false)
                val announcement = obj.optString("announcement", "")
                val kindStr = obj.optString("kind", "public")

                list.add(
                    Room(
                        id = id,
                        name = name,
                        slug = slug,
                        kind = if (kindStr == "group") RoomKind.GROUP else RoomKind.PUBLIC,
                        provinceCode = provinceCode,
                        provinceName = mapProvinceToRegion(provinceCode),
                        isLocked = isLocked,
                        announcement = announcement,
                        memberCount = 1,
                        onlineCount = 1,
                        isJoined = true
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.e(tag, "getRooms error", e)
            Result.failure(e)
        }
    }

    suspend fun createRoom(name: String, provinceCode: String, topic: String): Result<Room> = withContext(Dispatchers.IO) {
        try {
            val slug = name.lowercase().replace(" ", "-").replace("[^a-z0-9-]".toRegex(), "") + "-" + System.currentTimeMillis() % 10000
            val payload = JSONObject().apply {
                put("name", name)
                put("slug", slug)
                put("province_code", provinceCode.uppercase())
                put("announcement", topic)
                put("kind", "public")
                put("is_locked", false)
            }.toString()

            val url = "${SupabaseConfig.url}/rest/v1/rooms"
            val request = buildRequest(url)
                .header("Prefer", "return=representation")
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to create room: ${response.code} $body"))
            }

            val array = JSONArray(body)
            if (array.length() > 0) {
                val obj = array.getJSONObject(0)
                val newRoom = Room(
                    id = obj.getString("id"),
                    name = name,
                    slug = slug,
                    provinceCode = provinceCode.uppercase(),
                    provinceName = mapProvinceToRegion(provinceCode),
                    announcement = topic,
                    memberCount = 1,
                    onlineCount = 1
                )
                Result.success(newRoom)
            } else {
                Result.failure(Exception("Empty response when creating room"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRoomMessages(roomId: String): Result<List<RoomMessage>> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseConfig.url}/rest/v1/room_messages?room_id=eq.$roomId&select=*&order=created_at.asc"
            val request = buildRequest(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to load messages: ${response.code}"))
            }

            val array = JSONArray(body)
            val list = mutableListOf<RoomMessage>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val senderId = obj.optString("sender_id", obj.optString("user_id", ""))
                val msgBody = obj.optString("body", "")
                val kindStr = obj.optString("kind", "text")
                val createdAt = obj.optString("created_at", "")

                val kind = when (kindStr) {
                    "system" -> MessageKind.SYSTEM
                    "sticker" -> MessageKind.STICKER
                    else -> MessageKind.TEXT
                }

                list.add(
                    RoomMessage(
                        id = id,
                        roomId = roomId,
                        senderId = senderId,
                        senderName = if (kind == MessageKind.SYSTEM) "uzzapbot" else "User ${senderId.take(4)}",
                        senderAvatarHex = 0xFF0038A8,
                        senderRole = if (kind == MessageKind.SYSTEM) MemberRole.ADMIN else MemberRole.MEMBER,
                        body = msgBody,
                        kind = kind,
                        timestamp = if (createdAt.length >= 16) createdAt.substring(11, 16) else "Now"
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendRoomMessage(roomId: String, body: String, kind: String = "text", replyToId: String? = null): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply {
                put("room_id", roomId)
                put("sender_id", userId)
                put("body", body)
                put("kind", kind)
                if (!replyToId.isNullOrBlank()) {
                    put("reply_to_id", replyToId)
                }
            }.toString()

            val url = "${SupabaseConfig.url}/rest/v1/room_messages"
            val request = buildRequest(url)
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to send message: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfiles(): Result<List<Profile>> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseConfig.url}/rest/v1/profiles?select=*&order=last_seen_at.desc.nullslast"
            val request = buildRequest(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to fetch profiles: ${response.code}"))
            }

            val array = JSONArray(body)
            val list = mutableListOf<Profile>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val username = obj.optString("username", "user")
                val displayName = obj.optString("display_name", username)
                val bio = obj.optString("bio", "")
                val statusText = obj.optString("status_text", "Tambay online")
                val isActive = obj.optBoolean("is_active", false)

                list.add(
                    Profile(
                        id = id,
                        username = username,
                        displayName = displayName,
                        avatarInitial = displayName.take(1).uppercase(),
                        avatarColorHex = 0xFFCE1126,
                        bio = bio,
                        statusText = statusText,
                        province = "NCR",
                        isActive = isActive,
                        points = 100,
                        streak = 1
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getContents(): Result<List<ContentPost>> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseConfig.url}/rest/v1/contents?select=*,poll_options(*)&order=created_at.desc"
            val request = buildRequest(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to fetch contents: ${response.code}"))
            }

            val array = JSONArray(body)
            val list = mutableListOf<ContentPost>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val title = obj.optString("title", "")
                val bodyText = obj.optString("body", "")
                val createdAt = obj.optString("created_at", "")
                val userId = obj.optString("author_id", obj.optString("user_id", ""))

                val pollOptionsArray = obj.optJSONArray("poll_options")
                val poll = if (pollOptionsArray != null && pollOptionsArray.length() > 0) {
                    val opts = mutableListOf<PollOption>()
                    for (j in 0 until pollOptionsArray.length()) {
                        val pOpt = pollOptionsArray.getJSONObject(j)
                        opts.add(
                            PollOption(
                                id = pOpt.optString("id", "$j"),
                                text = pOpt.optString("text", pOpt.optString("title", "Option $j")),
                                votes = pOpt.optInt("vote_count", pOpt.optInt("votes", 0))
                            )
                        )
                    }
                    PollData(
                        id = "poll_$id",
                        question = title,
                        options = opts,
                        totalVotes = opts.sumOf { it.votes }
                    )
                } else null

                list.add(
                    ContentPost(
                        id = id,
                        author = Profile(
                            id = userId,
                            username = "tambay_${userId.take(4)}",
                            displayName = "Tambay",
                            avatarInitial = "T",
                            avatarColorHex = 0xFF0038A8,
                            bio = "Tambay",
                            statusText = "Active",
                            province = "NCR"
                        ),
                        title = title,
                        body = bodyText,
                        category = "General",
                        poll = poll,
                        createdAt = if (createdAt.length >= 10) createdAt.substring(0, 10) else "Recent",
                        likesCount = 0,
                        commentsCount = 0
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createContent(title: String, body: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply {
                put("title", title)
                put("body", body)
                put("author_id", userId)
                put("kind", "post")
                put("is_published", true)
            }.toString()

            val url = "${SupabaseConfig.url}/rest/v1/contents"
            val request = buildRequest(url)
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to post: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitReport(targetId: String, reason: String, details: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: "anonymous"
            val payload = JSONObject().apply {
                put("reporter_id", userId)
                put("target_id", targetId)
                put("reason", reason)
                put("details", details)
            }.toString()

            val url = "${SupabaseConfig.url}/rest/v1/reports"
            val request = buildRequest(url)
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun blockUser(targetUserId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.success(true)
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("target_user_id", targetUserId)
                put("status", "blocked")
            }.toString()

            val url = "${SupabaseConfig.url}/rest/v1/relationships"
            val request = buildRequest(url)
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addBuddy(targetUserId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply {
                put("user_id", userId)
                put("target_user_id", targetUserId)
                put("status", "accepted")
            }.toString()

            val url = "${SupabaseConfig.url}/rest/v1/relationships"
            val request = buildRequest(url)
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapProvinceToRegion(code: String): String {
        return when (code.uppercase()) {
            "NCR", "MNL" -> "National Capital Region"
            "CEB" -> "Central Visayas (Region VII)"
            "DVO" -> "Davao Region (Region XI)"
            "PAM", "BUL", "TAR" -> "Central Luzon (Region III)"
            "ILO", "NEG" -> "Western Visayas (Region VI)"
            "ALB", "CAM" -> "Bicol Region (Region V)"
            "BAG", "BEN" -> "Cordillera (CAR)"
            "ZAM" -> "Zamboanga Peninsula (Region IX)"
            "CDO", "MIS" -> "Northern Mindanao (Region X)"
            else -> "Philippines"
        }
    }
}
