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
        .authenticator(SupabaseTokenAuthenticator(authManager))
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
            val rpcResult = callRpc("list_public_chats", JSONObject().apply {
                put("p_limit", 100)
                put("p_offset", 0)
            })
            if (rpcResult.isFailure) return@withContext Result.failure(rpcResult.exceptionOrNull()!!)

            val publicRooms = JSONArray(rpcResult.getOrNull().orEmpty())
            if (publicRooms.length() == 0) return@withContext Result.success(emptyList())

            val ids = buildList {
                for (i in 0 until publicRooms.length()) {
                    publicRooms.getJSONObject(i).optString("id").takeIf { it.isNotBlank() }?.let(::add)
                }
            }
            val idFilter = ids.joinToString(",")
            val detailUrl = "${SupabaseConfig.url}/rest/v1/rooms?select=id,slug,name,description,kind,province_code,is_locked,announcement,view_only,members_can_invite,pinned_message_id&id=in.($idFilter)"
            val detailResponse = client.newCall(buildRequest(detailUrl).get().build()).execute()
            val detailBody = detailResponse.body?.string().orEmpty()
            if (!detailResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to fetch room details: ${detailResponse.code}"))
            }
            val details = mutableMapOf<String, JSONObject>()
            val detailArray = JSONArray(detailBody)
            for (i in 0 until detailArray.length()) {
                val obj = detailArray.getJSONObject(i)
                details[obj.optString("id")] = obj
            }

            val userId = authManager.getCurrentUserId()
            val memberships = mutableMapOf<String, JSONObject>()
            if (!userId.isNullOrBlank()) {
                val memberUrl = "${SupabaseConfig.url}/rest/v1/room_members?user_id=eq.$userId&room_id=in.($idFilter)&select=room_id,role,is_pinned"
                val memberResponse = client.newCall(buildRequest(memberUrl).get().build()).execute()
                val memberBody = memberResponse.body?.string().orEmpty()
                if (!memberResponse.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to fetch room membership: ${memberResponse.code}"))
                }
                val memberArray = JSONArray(memberBody)
                for (i in 0 until memberArray.length()) {
                    val obj = memberArray.getJSONObject(i)
                    memberships[obj.optString("room_id")] = obj
                }
            }

            val list = mutableListOf<Room>()
            for (i in 0 until publicRooms.length()) {
                val summary = publicRooms.getJSONObject(i)
                val id = summary.optString("id", "")
                if (id.isBlank()) continue
                val detail = details[id]
                val membership = memberships[id]
                val role = when (membership?.optString("role", "member")?.lowercase()) {
                    "owner" -> MemberRole.OWNER
                    "admin" -> MemberRole.ADMIN
                    "moderator", "mod" -> MemberRole.MODERATOR
                    else -> MemberRole.MEMBER
                }
                val provinceCode = summary.optString("province_code", detail?.optString("province_code", "ALL") ?: "ALL")
                list.add(
                    Room(
                        id = id,
                        name = summary.optString("name", detail?.optString("name", "Room") ?: "Room"),
                        slug = summary.optString("slug", detail?.optString("slug", id) ?: id),
                        kind = RoomKind.PUBLIC,
                        provinceCode = provinceCode,
                        provinceName = mapProvinceToRegion(provinceCode),
                        isLocked = detail?.optBoolean("is_locked", false) ?: false,
                        viewOnly = detail?.optBoolean("view_only", false) ?: false,
                        membersCanInvite = detail?.optBoolean("members_can_invite", true) ?: true,
                        announcement = detail?.optString("announcement") ?: detail?.optString("description") ?: "",
                        memberCount = summary.optLong("member_count", 0).toInt(),
                        onlineCount = summary.optLong("online_count", 0).toInt(),
                        isJoined = membership != null,
                        isPinned = membership?.optBoolean("is_pinned", false) ?: false,
                        myRole = role,
                        colorHex = 0xFFB7F34A
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
            val slug = name.lowercase()
                .replace(" ", "-")
                .replace("[^a-z0-9-]".toRegex(), "")
                .trim('-') + "-" + System.currentTimeMillis() % 10000
            val result = callRpc("create_room", JSONObject().apply {
                put("p_slug", slug)
                put("p_name", name)
                put("p_description", topic)
                put("p_kind", "public")
                put("p_province_code", provinceCode.uppercase())
            })
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)
            val obj = JSONObject(result.getOrNull().orEmpty())
            Result.success(Room(
                id = obj.optString("id"),
                name = obj.optString("name", name),
                slug = obj.optString("slug", slug),
                kind = RoomKind.PUBLIC,
                provinceCode = obj.optString("province_code", provinceCode.uppercase()),
                provinceName = mapProvinceToRegion(obj.optString("province_code", provinceCode.uppercase())),
                isLocked = obj.optBoolean("is_locked", false),
                announcement = obj.optString("announcement", topic),
                memberCount = 0,
                onlineCount = 0,
                isJoined = false
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRoomMessages(roomId: String): Result<List<RoomMessage>> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseConfig.url}/rest/v1/room_messages?room_id=eq.$roomId&select=*,sender:profiles!room_messages_sender_id_fkey(*),reactions:room_message_reactions(*)&order=created_at.asc"
            val request = buildRequest(url).get().build()
            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to load messages: ${response.code}"))
            }

            val array = JSONArray(body)
            val list = mutableListOf<RoomMessage>()
            val currentUserId = authManager.getCurrentUserId()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val senderId = obj.optString("sender_id", "")
                val msgBody = obj.optString("body", "")
                val kindStr = obj.optString("kind", "text").lowercase()
                val createdAt = obj.optString("created_at", "")
                val deleted = !obj.isNull("deleted_at")
                val sender = obj.optJSONObject("sender")
                val senderName = sender?.optString("display_name")?.ifBlank { sender.optString("username") }?.ifBlank { "Tambay" }
                    ?: if (kindStr == "system") "PHChat" else "User"

                val kind = when (kindStr) {
                    "system" -> MessageKind.SYSTEM
                    "sticker" -> MessageKind.STICKER
                    "media" -> MessageKind.MEDIA
                    "reply" -> MessageKind.REPLY
                    else -> MessageKind.TEXT
                }

                val reactions = mutableMapOf<String, Int>()
                var myReaction: String? = null
                val reactionArray = obj.optJSONArray("reactions")
                if (reactionArray != null) {
                    for (j in 0 until reactionArray.length()) {
                        val reaction = reactionArray.getJSONObject(j)
                        val emoji = reaction.optString("reaction", "")
                        if (emoji.isBlank()) continue
                        reactions[emoji] = (reactions[emoji] ?: 0) + 1
                        if (reaction.optString("user_id") == currentUserId) myReaction = emoji
                    }
                }

                list.add(
                    RoomMessage(
                        id = id,
                        roomId = roomId,
                        senderId = senderId,
                        senderName = senderName,
                        senderAvatarHex = 0xFFB7F34A,
                        senderRole = if (kind == MessageKind.SYSTEM) MemberRole.ADMIN else MemberRole.MEMBER,
                        body = if (deleted) "[Message deleted]" else msgBody,
                        kind = kind,
                        stickerEmoji = obj.optJSONObject("metadata")?.optString("sticker_emoji"),
                        reactions = reactions,
                        myReaction = myReaction,
                        timestamp = if (createdAt.length >= 16) createdAt.substring(11, 16) else "Now",
                        isEdited = !obj.isNull("edited_at"),
                        isDeleted = deleted
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendRoomMessage(roomId: String, body: String, kind: String = "text", replyToId: String? = null): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("send_room_message", JSONObject().apply {
            put("p_room_id", roomId)
            put("p_body", body)
            put("p_kind", kind)
            if (replyToId.isNullOrBlank()) put("p_reply_to_id", JSONObject.NULL) else put("p_reply_to_id", replyToId)
            put("p_metadata", JSONObject())
        }).map { true }
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
                val statusText = obj.optString("status_text", "Online")
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
                        province = obj.optString("province", "Philippines").ifBlank { "Philippines" },
                        isActive = isActive,
                        points = 0,
                        streak = 0
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
            val result = callRpc("list_content_feed_v2", JSONObject().apply {
                put("p_room_id", JSONObject.NULL)
                put("p_author_id", JSONObject.NULL)
                put("p_before_created_at", JSONObject.NULL)
                put("p_before_id", JSONObject.NULL)
                put("p_limit", 50)
            })
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)

            val root = JSONObject(result.getOrNull().orEmpty())
            val array = root.optJSONArray("items") ?: JSONArray()
            val list = mutableListOf<ContentPost>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val title = obj.optString("title", "")
                val bodyText = obj.optString("body", "")
                val createdAt = obj.optString("created_at", "")
                val userId = obj.optString("author_id", "")
                val metadata = obj.optJSONObject("metadata")
                val author = obj.optJSONObject("author")?.let { profileToModel(it) }
                    ?: Profile(
                        id = userId,
                        username = "user",
                        displayName = "User",
                        avatarInitial = "U",
                        avatarColorHex = 0xFF00A94F,
                        bio = "",
                        statusText = "",
                        province = "Philippines"
                    )

                val pollOptionsArray = obj.optJSONArray("poll_options")
                val poll = if (pollOptionsArray != null && pollOptionsArray.length() > 0) {
                    val opts = mutableListOf<PollOption>()
                    var selectedOptionId: String? = null
                    for (j in 0 until pollOptionsArray.length()) {
                        val pOpt = pollOptionsArray.getJSONObject(j)
                        if (pOpt.optBoolean("is_selected", false)) selectedOptionId = pOpt.optString("id")
                        opts.add(
                            PollOption(
                                id = pOpt.optString("id", "$j"),
                                text = pOpt.optString("label", "Option $j"),
                                votes = pOpt.optInt("vote_count", 0)
                            )
                        )
                    }
                    PollData(
                        id = "poll_$id",
                        question = title,
                        options = opts,
                        totalVotes = opts.sumOf { it.votes },
                        hasVoted = selectedOptionId != null,
                        selectedOptionId = selectedOptionId
                    )
                } else null

                val kind = when (obj.optString("kind", "post").lowercase()) {
                    "poll" -> ContentKind.POLL
                    "announcement" -> ContentKind.ANNOUNCEMENT
                    else -> ContentKind.POST
                }

                list.add(
                    ContentPost(
                        id = id,
                        author = author,
                        title = title,
                        body = bodyText,
                        category = metadata?.optString("category", "General")?.ifBlank { "General" } ?: "General",
                        kind = kind,
                        poll = poll,
                        likesCount = obj.optInt("likes_count", 0),
                        isLiked = obj.optBoolean("is_liked", false),
                        isSaved = obj.optBoolean("is_saved", false),
                        commentsCount = obj.optInt("comments_count", 0),
                        createdAt = if (createdAt.length >= 10) createdAt.substring(0, 10) else "Recent"
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createContent(title: String, body: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val result = callRpc("create_content", JSONObject().apply {
            put("p_room_id", JSONObject.NULL)
            put("p_kind", "post")
            put("p_title", title)
            put("p_body", body)
            put("p_metadata", JSONObject())
        })
        result.map { true }
    }

    suspend fun submitReport(targetId: String, reason: String, details: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val payload = JSONObject().apply {
                put("reporter_id", userId); put("target_id", targetId); put("reason", reason); put("details", details)
            }.toString()
            val response = client.newCall(buildRequest("${SupabaseConfig.url}/rest/v1/reports")
                .header("Content-Type", "application/json")
                .post(payload.toRequestBody(jsonMediaType)).build()).execute()
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Report failed: ${response.code}"))
        } catch (e: Exception) { Result.failure(e) }
    }

suspend fun blockUser(targetUserId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("toggle_block", JSONObject().apply { put("p_target_user_id", targetUserId) }).map { true }
    }

suspend fun addBuddy(targetUserId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("toggle_follow", JSONObject().apply { put("p_target_user_id", targetUserId) }).map { true }
    }

suspend fun getConversations(): Result<List<Conversation>> = withContext(Dispatchers.IO) {
        try {
            val result = callRpc("list_conversations")
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)
            val array = JSONArray(result.getOrNull().orEmpty())
            val out = mutableListOf<Conversation>()
            for (i in 0 until array.length()) {
                val row = array.getJSONObject(i)
                val participant = Profile(
                    id = row.optString("participant_id", ""),
                    username = row.optString("participant_username", "user"),
                    displayName = row.optString("participant_display_name", row.optString("participant_username", "user")),
                    avatarInitial = row.optString("participant_display_name", "T").take(1).uppercase(),
                    avatarColorHex = 0xFF00A94F,
                    bio = row.optString("participant_bio", ""),
                    statusText = row.optString("participant_status_text", ""),
                    province = "Philippines",
                    isActive = row.optBoolean("participant_is_active", false),
                    lastSeenAt = row.optString("participant_last_seen_at", "")
                )
                out.add(
                    Conversation(
                        id = row.optString("id", ""),
                        participant = participant,
                        lastMessage = row.optString("last_message", ""),
                        lastMessageTime = row.optString("last_message_at", ""),
                        unreadCount = row.optLong("unread_count", 0).toInt(),
                        isPinned = row.optBoolean("is_pinned", false)
                    )
                )
            }
            Result.success(out)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDirectMessages(conversationId: String): Result<List<DirectMessage>> = withContext(Dispatchers.IO) {
        try {
            val result = callRpc("list_conversation_messages", JSONObject().apply {
                put("p_conversation_id", conversationId)
                put("p_before_created_at", JSONObject.NULL)
                put("p_before_id", JSONObject.NULL)
                put("p_limit", 100)
            })
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)
            val root = JSONObject(result.getOrNull().orEmpty())
            val items = root.optJSONArray("items") ?: JSONArray()
            val out = mutableListOf<DirectMessage>()
            for (i in items.length() - 1 downTo 0) {
                val o = items.getJSONObject(i)
                val metadata = o.optJSONObject("metadata")
                out.add(DirectMessage(
                    o.optString("id"), conversationId, o.optString("sender_id"),
                    o.optString("body", ""), when (o.optString("kind")) {
                        "sticker" -> MessageKind.STICKER
                        "system" -> MessageKind.SYSTEM
                        "media" -> MessageKind.MEDIA
                        "reply" -> MessageKind.REPLY
                        else -> MessageKind.TEXT
                    },
                    metadata?.optString("sticker_emoji"),
                    o.optString("created_at", "")
                ))
            }
            Result.success(out)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun markConversationRead(conversationId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("mark_conversation_read", JSONObject().apply {
            put("p_conversation_id", conversationId)
            put("p_read_at", java.time.Instant.now().toString())
        }).map { true }
    }

    suspend fun markMessageRead(messageId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("mark_message_read", JSONObject().apply {
            put("p_message_id", messageId)
        }).map { true }
    }

    suspend fun editDirectMessage(messageId: String, body: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("edit_conversation_message", JSONObject().apply {
            put("p_message_id", messageId)
            put("p_body", body)
        }).map { true }
    }

    suspend fun deleteDirectMessage(messageId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("delete_conversation_message", JSONObject().apply {
            put("p_message_id", messageId)
        }).map { true }
    }

    suspend fun replyToDirectMessage(conversationId: String, replyToId: String, body: String): Result<DirectMessage> = withContext(Dispatchers.IO) {
        try {
            val result = callRpc("reply_to_conversation_message", JSONObject().apply {
                put("p_conversation_id", conversationId)
                put("p_reply_to_id", replyToId)
                put("p_body", body)
                put("p_metadata", JSONObject())
            })
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)
            val o = JSONObject(result.getOrNull().orEmpty())
            Result.success(DirectMessage(
                o.optString("id"), conversationId, o.optString("sender_id"),
                o.optString("body", body),
                MessageKind.REPLY,
                null,
                o.optString("created_at", "")
            ))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun createDirectConversation(targetUserId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            if (userId == targetUserId) return@withContext Result.failure(Exception("Cannot message yourself"))
            val result = callRpc("create_conversation", JSONObject().apply {
                put("p_kind", "private")
                put("p_title", JSONObject.NULL)
                put("p_member_ids", JSONArray().apply {
                    put(targetUserId)
                })
            })
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)
            Result.success(JSONObject(result.getOrNull().orEmpty()).optString("id"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun sendDirectMessage(conversationId: String, body: String, kind: String = "text", stickerEmoji: String? = null, replyToId: String? = null): Result<DirectMessage> = withContext(Dispatchers.IO) {
        try {
            val result = callRpc("send_conversation_message", JSONObject().apply {
                put("p_conversation_id", conversationId)
                put("p_body", body)
                put("p_kind", kind)
                if (replyToId.isNullOrBlank()) put("p_reply_to_id", JSONObject.NULL) else put("p_reply_to_id", replyToId)
                put("p_metadata", JSONObject().apply {
                    if (stickerEmoji != null) put("sticker_emoji", stickerEmoji)
                })
            })
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)
            val o = JSONObject(result.getOrNull().orEmpty())
            Result.success(DirectMessage(
                o.optString("id"), conversationId, o.optString("sender_id"),
                o.optString("body", body),
                when (o.optString("kind")) {
                    "sticker" -> MessageKind.STICKER
                    "media" -> MessageKind.MEDIA
                    "reply" -> MessageKind.REPLY
                    else -> MessageKind.TEXT
                },
                o.optJSONObject("metadata")?.optString("sticker_emoji") ?: stickerEmoji,
                o.optString("created_at", "")
            ))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun toggleContentReaction(contentId: String, reaction: String = "like"): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("toggle_content_reaction", JSONObject().apply {
            put("p_content_id", contentId)
            put("p_reaction", reaction)
        }).map { true }
    }

    suspend fun toggleContentSave(contentId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("toggle_content_save", JSONObject().apply {
            put("p_content_id", contentId)
        }).map { true }
    }

    suspend fun votePoll(contentId: String, optionId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("vote_content_poll", JSONObject().apply {
            put("p_content_id", contentId)
            put("p_option_id", optionId)
        }).map { true }
    }

    suspend fun addProfileComment(profileId: String, body: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("add_profile_comment", JSONObject().apply {
            put("p_profile_id", profileId)
            put("p_body", body)
            put("p_parent_id", JSONObject.NULL)
        }).map { true }
    }

    suspend fun toggleFollow(targetUserId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("toggle_follow", JSONObject().apply { put("p_target_user_id", targetUserId) }).map { true }
    }

    suspend fun toggleBlock(targetUserId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("toggle_block", JSONObject().apply { put("p_target_user_id", targetUserId) }).map { true }
    }

    suspend fun toggleFavorite(targetUserId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("toggle_favorite", JSONObject().apply { put("p_target_user_id", targetUserId) }).map { true }
    }

    suspend fun checkInToday(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = callRpc("check_in")
            if (response.isFailure) return@withContext Result.failure(response.exceptionOrNull()!!)
            val obj = JSONObject(response.getOrNull().orEmpty())
            Result.success(!obj.optBoolean("already_checked_in", false))
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

    suspend fun updateRoomMessageDeleted(messageId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("delete_room_message", JSONObject().apply {
            put("p_message_id", messageId)
        }).map { true }
    }

    suspend fun setRoomPinned(roomId: String, pinned: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("set_room_member_chat_preferences", JSONObject().apply {
            put("p_room_id", roomId)
            put("p_notifications_enabled", JSONObject.NULL)
            put("p_is_pinned", pinned)
        }).map { true }
    }

    suspend fun setRoomChatSettings(
        roomId: String,
        announcement: String,
        viewOnly: Boolean,
        membersCanInvite: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("set_room_chat_settings", JSONObject().apply {
            put("p_room_id", roomId)
            put("p_announcement", announcement)
            put("p_view_only", viewOnly)
            put("p_members_can_invite", membersCanInvite)
        }).map { true }
    }

    suspend fun setRoomLock(roomId: String, locked: Boolean, reason: String? = null): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("set_room_lock", JSONObject().apply {
            put("p_room_id", roomId)
            put("p_locked", locked)
            if (reason.isNullOrBlank()) put("p_reason", JSONObject.NULL) else put("p_reason", reason)
        }).map { true }
    }

    suspend fun toggleRoomMembership(roomId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val membershipUrl = "${SupabaseConfig.url}/rest/v1/room_members?room_id=eq.$roomId&user_id=eq.$userId"
            val existing = client.newCall(buildRequest(membershipUrl).get().build()).execute()
            val body = existing.body?.string().orEmpty()
            if (!existing.isSuccessful) return@withContext Result.failure(Exception("Membership lookup failed: ${existing.code}"))
            val joined = JSONArray(body).length() > 0
            val result = if (joined) {
                callRpc("leave_room", JSONObject().apply { put("p_room_id", roomId) })
            } else {
                callRpc("join_room", JSONObject().apply { put("p_room_id", roomId) })
            }
            result.map { !joined }
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
            val result = callRpc("list_notifications", JSONObject().apply {
                put("p_before_created_at", JSONObject.NULL)
                put("p_before_id", JSONObject.NULL)
                put("p_limit", 100)
            })
            if (result.isFailure) return@withContext Result.failure(result.exceptionOrNull()!!)
            val root = JSONObject(result.getOrNull().orEmpty())
            val arr = root.optJSONArray("items") ?: JSONArray()
            val out = mutableListOf<NotificationItem>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val payload = o.optJSONObject("payload") ?: JSONObject()
                out.add(NotificationItem(
                    id = o.optString("id"),
                    type = o.optString("type"),
                    title = payload.optString("title", o.optString("type")),
                    message = payload.optString("message", payload.optString("body", "")),
                    timestamp = o.optString("created_at"),
                    isRead = !o.isNull("read_at"),
                    targetRoomId = payload.optString("room_id").takeIf { it.isNotBlank() }
                ))
            }
            Result.success(out)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun markNotificationsRead(): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("mark_all_notifications_read").map { true }
    }

    suspend fun clearNotifications(): Result<Boolean> = withContext(Dispatchers.IO) {
        callRpc("clear_notifications").map { true }
    }

    private fun profileToModel(obj: JSONObject): Profile {
        val displayName = obj.optString("display_name").ifBlank { obj.optString("username", "user") }
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

    suspend fun getNotificationPreferences(): Result<NotificationPreferences> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val url = "${SupabaseConfig.url}/rest/v1/notification_preferences?user_id=eq.$userId&select=*"
            val resp = client.newCall(buildRequest(url).get().build()).execute()
            val body = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) return@withContext Result.failure(Exception("Failed to load notification preferences: ${resp.code}"))
            if (JSONArray(body).length() == 0) {
                return@withContext Result.success(NotificationPreferences(userId = userId))
            }
            val o = JSONArray(body).getJSONObject(0)
            Result.success(NotificationPreferences(
                userId = userId,
                followEnabled = o.optBoolean("follow_enabled", true),
                blockEnabled = o.optBoolean("block_enabled", true),
                contentCommentEnabled = o.optBoolean("content_comment_enabled", true),
                commentReplyEnabled = o.optBoolean("comment_reply_enabled", true),
                contentReactionEnabled = o.optBoolean("content_reaction_enabled", true),
                roomMessageReactionEnabled = o.optBoolean("room_message_reaction_enabled", true),
                profileCommentEnabled = o.optBoolean("profile_comment_enabled", true),
                mentionEnabled = o.optBoolean("mention_enabled", true),
                roomInviteEnabled = o.optBoolean("room_invite_enabled", true),
                conversationInviteEnabled = o.optBoolean("conversation_invite_enabled", true)
            ))
        } catch (e: Exception) {
            Log.e(tag, "getNotificationPreferences error", e)
            Result.failure(e)
        }
    }

    suspend fun updateNotificationPreference(column: String, enabled: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val current = getNotificationPreferences()
            if (current.isFailure) return@withContext Result.failure(current.exceptionOrNull()!!)
            val p = current.getOrNull() ?: NotificationPreferences()
            val values = mapOf(
                "follow_enabled" to p.followEnabled,
                "block_enabled" to p.blockEnabled,
                "content_comment_enabled" to p.contentCommentEnabled,
                "comment_reply_enabled" to p.commentReplyEnabled,
                "content_reaction_enabled" to p.contentReactionEnabled,
                "room_message_reaction_enabled" to p.roomMessageReactionEnabled,
                "profile_comment_enabled" to p.profileCommentEnabled,
                "mention_enabled" to p.mentionEnabled,
                "room_invite_enabled" to p.roomInviteEnabled,
                "conversation_invite_enabled" to p.conversationInviteEnabled
            ).toMutableMap()
            if (!values.containsKey(column)) return@withContext Result.failure(Exception("Invalid notification preference"))
            values[column] = enabled
            callRpc("set_notification_preferences", JSONObject().apply {
                put("p_follow_enabled", values["follow_enabled"]!!)
                put("p_block_enabled", values["block_enabled"]!!)
                put("p_content_comment_enabled", values["content_comment_enabled"]!!)
                put("p_comment_reply_enabled", values["comment_reply_enabled"]!!)
                put("p_content_reaction_enabled", values["content_reaction_enabled"]!!)
                put("p_room_message_reaction_enabled", values["room_message_reaction_enabled"]!!)
                put("p_profile_comment_enabled", values["profile_comment_enabled"]!!)
                put("p_mention_enabled", values["mention_enabled"]!!)
                put("p_room_invite_enabled", values["room_invite_enabled"]!!)
                put("p_conversation_invite_enabled", values["conversation_invite_enabled"]!!)
            }).map { true }
        } catch (e: Exception) {
            Log.e(tag, "updateNotificationPreference error", e)
            Result.failure(e)
        }
    }


    /**
     * Canonical backend operation bridge. Missing UI features use the existing
     * Supabase RPC contract instead of duplicating business rules in Compose.
     */
    suspend fun callRpc(functionName: String, payload: JSONObject = JSONObject()): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "${SupabaseConfig.url}/rest/v1/rpc/$functionName"
            val request = buildRequest(url)
                .header("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(jsonMediaType))
                .build()
            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                Result.failure(Exception("$functionName failed: ${response.code} $body"))
            } else {
                Result.success(body)
            }
        } catch (e: Exception) {
            Log.e(tag, "$functionName error", e)
            Result.failure(e)
        }
    }

    suspend fun advancedRpc(functionName: String, payload: JSONObject = JSONObject()): Result<String> =
        callRpc(functionName, payload)

    suspend fun uploadMedia(
        context: android.content.Context,
        uri: android.net.Uri,
        bucket: String = "room-media",
        relation: JSONObject = JSONObject()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val userId = authManager.getCurrentUserId() ?: return@withContext Result.failure(Exception("Must be logged in"))
            val resolver = context.contentResolver
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@withContext Result.failure(Exception("Unable to read selected media"))
            val mime = resolver.getType(uri) ?: "application/octet-stream"
            val name = "user_$userId/${System.currentTimeMillis()}_${java.util.UUID.randomUUID()}"
            val objectUrl = "${SupabaseConfig.url}/storage/v1/object/$bucket/$name"
            val upload = buildRequest(objectUrl)
                .header("Content-Type", mime)
                .header("x-upsert", "false")
                .put(bytes.toRequestBody(mime.toMediaType()))
                .build()
            val uploadResponse = client.newCall(upload).execute()
            val uploadBody = uploadResponse.body?.string().orEmpty()
            if (!uploadResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Media upload failed: ${uploadResponse.code} $uploadBody"))
            }

            val metadata = JSONObject().apply {
                put("owner_id", userId)
                put("bucket", bucket)
                put("path", name)
                put("mime_type", mime)
                put("size_bytes", bytes.size)
                put("metadata", JSONObject())
                put("position", 0)
            }
            relation.keys().forEach { key -> metadata.put(key, relation.get(key)) }

            val mediaUrl = "${SupabaseConfig.url}/rest/v1/media"
            val mediaRequest = buildRequest(mediaUrl)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .post(metadata.toString().toRequestBody(jsonMediaType))
                .build()
            val mediaResponse = client.newCall(mediaRequest).execute()
            val mediaBody = mediaResponse.body?.string().orEmpty()
            if (!mediaResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Media metadata failed: ${mediaResponse.code} $mediaBody"))
            }
            Result.success("$objectUrl")
        } catch (e: Exception) {
            Log.e(tag, "uploadMedia error", e)
            Result.failure(e)
        }
    }

}