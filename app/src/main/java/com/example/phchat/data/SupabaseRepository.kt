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
        return authManager.getAccessToken() ?: throw IllegalStateException("Authentication required")
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
                        memberCount = 0,
                        onlineCount = 0,
                        isJoined = false
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
                put("metadata", JSONObject())
                put("is_published", true)
                put("is_featured", false)
                put("is_hidden", false)
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

    suspend fun getConversations(): Result<List<Conversation>> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val url = "${SupabaseConfig.url}/rest/v1/conversation_members?user_id=eq.$userId&select=conversation_id,conversations(id,title,kind,updated_at,conversation_members(user_id,profiles(*)))&order=joined_at.desc"
            val response = client.newCall(buildRequest(url).get().build()).execute()
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Failed to fetch conversations: ${response.code}"))
            val array = JSONArray(body)
            val out = mutableListOf<Conversation>()
            for (i in 0 until array.length()) {
                val row = array.getJSONObject(i)
                val conv = row.optJSONObject("conversations") ?: continue
                val members = conv.optJSONArray("conversation_members") ?: continue
                var participant: Profile? = null
                for (j in 0 until members.length()) {
                    val member = members.optJSONObject(j) ?: continue
                    if (member.optString("user_id") != userId) {
                        val profile = member.optJSONObject("profiles")
                        if (profile != null) participant = profileToModel(profile)
                    }
                }
                if (participant != null) {
                    out.add(Conversation(
                        id = conv.optString("id"),
                        participant = participant,
                        lastMessage = "",
                        lastMessageTime = conv.optString("updated_at", ""),
                        unreadCount = 0
                    ))
                }
            }
            Result.success(out.distinctBy { it.id })
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getDirectMessages(conversationId: String): Result<List<DirectMessage>> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseConfig.url}/rest/v1/conversation_messages?conversation_id=eq.$conversationId&select=*&order=created_at.asc"
            val response = client.newCall(buildRequest(url).get().build()).execute()
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Failed to load DM: ${response.code}"))
            val array = JSONArray(body)
            val out = mutableListOf<DirectMessage>()
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                out.add(DirectMessage(o.optString("id"), conversationId, o.optString("sender_id"),
                    o.optString("body", ""), when (o.optString("kind")) {
                        "sticker" -> MessageKind.STICKER
                        "system" -> MessageKind.SYSTEM
                        "media" -> MessageKind.MEDIA
                        "reply" -> MessageKind.REPLY
                        else -> MessageKind.TEXT
                    }, o.optJSONObject("metadata")?.optString("sticker_emoji"), o.optString("created_at", "")))
            }
            Result.success(out)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun createDirectConversation(targetUserId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            if (userId == targetUserId) return@withContext Result.failure(Exception("Cannot message yourself"))
            val existingUrl = "${SupabaseConfig.url}/rest/v1/conversation_members?user_id=eq.$userId&select=conversation_id,conversations(id,kind,conversation_members(user_id))"
            val existingResponse = client.newCall(buildRequest(existingUrl).get().build()).execute()
            val existingBody = existingResponse.body?.string().orEmpty()
            if (existingResponse.isSuccessful) {
                val rows = JSONArray(existingBody)
                for (i in 0 until rows.length()) {
                    val conv = rows.optJSONObject(i)?.optJSONObject("conversations") ?: continue
                    if (conv.optString("kind") == "private") {
                        val members = conv.optJSONArray("conversation_members") ?: continue
                        if (members.length() == 2 && (0 until members.length()).any { members.optJSONObject(it)?.optString("user_id") == targetUserId }) {
                            return@withContext Result.success(conv.optString("id"))
                        }
                    }
                }
            }
            val convPayload = JSONObject().apply {
                put("kind", "private")
                put("created_by", userId)
            }.toString()
            val convReq = buildRequest("${SupabaseConfig.url}/rest/v1/conversations")
                .header("Prefer", "return=representation")
                .header("Content-Type", "application/json")
                .post(convPayload.toRequestBody(jsonMediaType)).build()
            val convResp = client.newCall(convReq).execute()
            val convBody = convResp.body?.string().orEmpty()
            if (!convResp.isSuccessful) return@withContext Result.failure(Exception("Failed to create conversation: ${convResp.code} $convBody"))
            val convId = JSONArray(convBody).getJSONObject(0).getString("id")
            val memberPayload = JSONArray().apply {
                put(JSONObject().apply { put("conversation_id", convId); put("user_id", userId); put("role", "owner") })
                put(JSONObject().apply { put("conversation_id", convId); put("user_id", targetUserId); put("role", "member") })
            }.toString()
            val memberReq = buildRequest("${SupabaseConfig.url}/rest/v1/conversation_members")
                .header("Content-Type", "application/json")
                .post(memberPayload.toRequestBody(jsonMediaType)).build()
            val memberResp = client.newCall(memberReq).execute()
            if (!memberResp.isSuccessful) return@withContext Result.failure(Exception("Failed to add conversation members: ${memberResp.code}"))
            Result.success(convId)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun sendDirectMessage(conversationId: String, body: String, kind: String = "text", stickerEmoji: String? = null, replyToId: String? = null): Result<DirectMessage> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply {
                put("conversation_id", conversationId); put("sender_id", userId); put("kind", kind); put("body", body); put("metadata", JSONObject())
                if (stickerEmoji != null) put("metadata", JSONObject().apply { put("sticker_emoji", stickerEmoji) })
                if (replyToId != null) put("reply_to_id", replyToId)
            }.toString()
            val req = buildRequest("${SupabaseConfig.url}/rest/v1/conversation_messages")
                .header("Prefer", "return=representation").header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType)).build()
            val resp = client.newCall(req).execute()
            val responseBody = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) return@withContext Result.failure(Exception("Failed to send DM: ${resp.code}"))
            val o = JSONArray(responseBody).getJSONObject(0)
            Result.success(DirectMessage(o.getString("id"), conversationId, userId, body,
                if (kind == "sticker") MessageKind.STICKER else MessageKind.TEXT, stickerEmoji, o.optString("created_at")))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun toggleContentReaction(contentId: String, reaction: String = "like"): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val base = "${SupabaseConfig.url}/rest/v1/content_reactions?content_id=eq.$contentId&user_id=eq.$userId"
            val existing = client.newCall(buildRequest(base).get().build()).execute()
            val existingBody = existing.body?.string().orEmpty()
            if (!existing.isSuccessful) return@withContext Result.failure(Exception("Reaction lookup failed: ${existing.code}"))
            if (JSONArray(existingBody).length() > 0) {
                val del = client.newCall(buildRequest(base).delete().build()).execute()
                return@withContext Result.success(del.isSuccessful.not().not())
            }
            val payload = JSONObject().apply { put("content_id", contentId); put("user_id", userId); put("reaction", reaction) }.toString()
            val add = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/content_reactions").header("Content-Type","application/json").post(payload.toRequestBody(jsonMediaType)).build()).execute()
            Result.success(add.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun toggleContentSave(contentId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val base = "${SupabaseConfig.url}/rest/v1/content_saves?content_id=eq.$contentId&user_id=eq.$userId"
            val existing = client.newCall(buildRequest(base).get().build()).execute()
            val body = existing.body?.string().orEmpty()
            if (!existing.isSuccessful) return@withContext Result.failure(Exception("Save lookup failed: ${existing.code}"))
            if (JSONArray(body).length() > 0) {
                val del = client.newCall(buildRequest(base).delete().build()).execute()
                return@withContext Result.success(del.isSuccessful)
            }
            val payload = JSONObject().apply { put("content_id", contentId); put("user_id", userId) }.toString()
            val add = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/content_saves").header("Content-Type","application/json").post(payload.toRequestBody(jsonMediaType)).build()).execute()
            Result.success(add.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun votePoll(contentId: String, optionId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val checkUrl = "${SupabaseConfig.url}/rest/v1/poll_votes?content_id=eq.$contentId&user_id=eq.$userId"
            val check = client.newCall(buildRequest(checkUrl).get().build()).execute()
            val checkBody = check.body?.string().orEmpty()
            if (!check.isSuccessful) return@withContext Result.failure(Exception("Vote lookup failed: ${check.code}"))
            if (JSONArray(checkBody).length() > 0) return@withContext Result.success(false)
            val payload = JSONObject().apply { put("content_id", contentId); put("option_id", optionId); put("user_id", userId) }.toString()
            val add = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/poll_votes").header("Content-Type","application/json").post(payload.toRequestBody(jsonMediaType)).build()).execute()
            Result.success(add.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addProfileComment(profileId: String, body: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply { put("profile_id", profileId); put("author_id", userId); put("body", body) }.toString()
            val resp = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/profile_comments").header("Content-Type","application/json").post(payload.toRequestBody(jsonMediaType)).build()).execute()
            Result.success(resp.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun toggleFollow(targetUserId: String): Result<Boolean> = toggleRelationship(targetUserId, "follow")
    suspend fun toggleBlock(targetUserId: String): Result<Boolean> = toggleRelationship(targetUserId, "block")

    private suspend fun toggleRelationship(targetUserId: String, kind: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val base = "${SupabaseConfig.url}/rest/v1/relationships?user_id=eq.$userId&target_user_id=eq.$targetUserId&kind=eq.$kind"
            val existing = client.newCall(buildRequest(base).get().build()).execute()
            val body = existing.body?.string().orEmpty()
            if (!existing.isSuccessful) return@withContext Result.failure(Exception("Relationship lookup failed: ${existing.code}"))
            if (JSONArray(body).length() > 0) {
                val del = client.newCall(buildRequest(base).delete().build()).execute()
                return@withContext Result.success(del.isSuccessful)
            }
            val payload = JSONObject().apply { put("user_id", userId); put("target_user_id", targetUserId); put("kind", kind) }.toString()
            val add = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/relationships").header("Content-Type","application/json").post(payload.toRequestBody(jsonMediaType)).build()).execute()
            Result.success(add.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun checkInToday(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val url = "${SupabaseConfig.url}/rest/v1/user_check_ins?user_id=eq.$userId&checkin_date=eq.${java.time.LocalDate.now()}"
            val existing = client.newCall(buildRequest(url).get().build()).execute()
            val body = existing.body?.string().orEmpty()
            if (!existing.isSuccessful) return@withContext Result.failure(Exception("Check-in lookup failed: ${existing.code}"))
            if (JSONArray(body).length() > 0) return@withContext Result.success(false)
            val payload = JSONObject().apply { put("user_id", userId); put("checkin_date", java.time.LocalDate.now().toString()); put("streak", 1); put("points", 50) }.toString()
            val add = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/user_check_ins").header("Content-Type","application/json").post(payload.toRequestBody(jsonMediaType)).build()).execute()
            Result.success(add.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateProfile(displayName: String, statusText: String, bio: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply { put("display_name", displayName); put("status_text", statusText); put("bio", bio); put("updated_at", java.time.Instant.now().toString()) }.toString()
            val req = buildRequest("${SupabaseConfig.url}/rest/v1/profiles?id=eq.$userId").header("Content-Type","application/json").patch(payload.toRequestBody(jsonMediaType)).build()
            val resp = client.newCall(req).execute()
            Result.success(resp.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateRoom(roomId: String, fields: JSONObject): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val req = buildRequest("${SupabaseConfig.url}/rest/v1/rooms?id=eq.$roomId").header("Content-Type","application/json").patch(fields.toString().toRequestBody(jsonMediaType)).build()
            val resp = client.newCall(req).execute()
            Result.success(resp.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateRoomMembership(roomId: String, joined: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val url = "${SupabaseConfig.url}/rest/v1/room_members?room_id=eq.$roomId&user_id=eq.$userId"
            if (joined) {
                val payload = JSONObject().apply { put("room_id", roomId); put("user_id", userId); put("role", "member") }.toString()
                val resp = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/room_members").header("Content-Type","application/json").post(payload.toRequestBody(jsonMediaType)).build()).execute()
                Result.success(resp.isSuccessful)
            } else {
                val resp = client.newCall(buildRequest(url).delete().build()).execute()
                Result.success(resp.isSuccessful)
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getNotifications(): Result<List<NotificationItem>> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val url = "${SupabaseConfig.url}/rest/v1/notifications?user_id=eq.$userId&select=*&order=created_at.desc"
            val resp = client.newCall(buildRequest(url).get().build()).execute()
            val body = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) return@withContext Result.failure(Exception("Failed to load notifications: ${resp.code}"))
            val arr = JSONArray(body)
            val out = mutableListOf<NotificationItem>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i); val payload = o.optJSONObject("payload") ?: JSONObject()
                out.add(NotificationItem(
                    id=o.optString("id"), type=o.optString("type"), title=payload.optString("title", o.optString("type")),
                    message=payload.optString("message", payload.optString("body","")), timestamp=o.optString("created_at"),
                    isRead=!o.isNull("read_at"), targetRoomId=payload.optString("room_id").takeIf { it.isNotBlank() }
                ))
            }
            Result.success(out)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun markNotificationsRead(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply { put("read_at", java.time.Instant.now().toString()) }.toString()
            val url = "${SupabaseConfig.url}/rest/v1/notifications?user_id=eq.$userId&read_at=is.null"
            val resp = client.newCall(buildRequest(url).header("Content-Type","application/json").patch(payload.toRequestBody(jsonMediaType)).build()).execute()
            Result.success(resp.isSuccessful)
        } catch (e: Exception) { Result.failure(e) }
    }

    private fun profileToModel(obj: JSONObject): Profile {
        val displayName = obj.optString("display_name").ifBlank { obj.optString("username", "Tambay") }
        return Profile(
            id = obj.optString("id"), username = obj.optString("username", "tambay"),
            displayName = displayName, avatarInitial = displayName.take(1).uppercase(),
            avatarColorHex = 0xFF0038A8, bio = obj.optString("bio", ""),
            statusText = obj.optString("status_text", ""), province = "NCR",
            isActive = obj.optBoolean("is_active", true), lastSeenAt = obj.optString("last_seen_at", "")
        )
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
