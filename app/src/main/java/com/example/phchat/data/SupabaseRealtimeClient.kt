package com.example.phchat.data

import android.util.Log
import com.example.phchat.model.MessageKind
import com.example.phchat.model.ReplySummary
import com.example.phchat.model.RoomMessage
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class SupabaseRealtimeClient(
    private val anonKey: String = SupabaseConfig.publishableKey,
    private val accessTokenProvider: (() -> String?)? = null
) {
    private val tag = "SupabaseRealtime"
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .pingInterval(25, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var heartbeatJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val refCounter = AtomicInteger(1)
    private var currentRoomId: String? = null
    private var currentConversationId: String? = null
    private var onNewMessageCallback: ((RoomMessage) -> Unit)? = null
    private var onNewDirectMessageCallback: ((com.example.phchat.model.DirectMessage) -> Unit)? = null
    private var onDataChangedCallback: ((table: String, id: String?) -> Unit)? = null
    private var shouldReconnect = false
    private var reconnectJob: Job? = null
    private var reconnectAttempt = 0

    fun connectAndSubscribeRoom(
        roomId: String,
        onNewMessage: (RoomMessage) -> Unit,
        onDataChanged: ((table: String, id: String?) -> Unit)? = null
    ) {
        disconnect()
        shouldReconnect = true
        currentRoomId = roomId
        currentConversationId = null
        onNewDirectMessageCallback = null
        onNewMessageCallback = onNewMessage
        onDataChangedCallback = onDataChanged
        openSocket()
    }

    fun connectAndSubscribeConversation(
        conversationId: String,
        onNewMessage: (com.example.phchat.model.DirectMessage) -> Unit,
        onDataChanged: ((table: String, id: String?) -> Unit)? = null
    ) {
        disconnect()
        shouldReconnect = true
        currentConversationId = conversationId
        currentRoomId = null
        onNewDirectMessageCallback = onNewMessage
        onNewMessageCallback = null
        onDataChangedCallback = onDataChanged
        openSocket()
    }

    fun connectAndSubscribeNotifications(
        onDataChanged: (table: String, id: String?) -> Unit
    ) {
        disconnect()
        shouldReconnect = true
        currentRoomId = null
        currentConversationId = null
        onNewDirectMessageCallback = null
        onNewMessageCallback = null
        onDataChangedCallback = onDataChanged
        openSocket()
    }

    private fun openSocket() {
        val wsUrl = "${SupabaseConfig.url.replaceFirst("https://", "wss://")}/realtime/v1/websocket?apikey=$anonKey&vsn=1.0.0"
        val request = Request.Builder().url(wsUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                reconnectAttempt = 0
                reconnectJob?.cancel()
                reconnectJob = null
                Log.d(tag, "Realtime WebSocket opened")
                startHeartbeat()
                when {
                    currentRoomId != null -> joinRoomChannel(currentRoomId!!)
                    currentConversationId != null -> joinConversationChannel(currentConversationId!!)
                    else -> joinNotificationsChannel()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "Realtime WebSocket failure: ${t.message}")
                scheduleReconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(tag, "Realtime WebSocket closed: $reason ($code)")
                if (shouldReconnect) scheduleReconnect()
            }
        })
    }

    private fun joinConversationChannel(conversationId: String) {
        val ref = refCounter.getAndIncrement().toString()
        val topic = "realtime:public:conversation_messages:conversation_id=eq.$conversationId"
        val postgresChanges = org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("event", "*")
                put("schema", "public")
                put("table", "conversation_messages")
                put("filter", "conversation_id=eq.$conversationId")
            })
            put(JSONObject().apply {
                put("event", "*")
                put("schema", "public")
                put("table", "notifications")
            })
        }
        val joinPayload = JSONObject().apply {
            put("topic", topic)
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                accessTokenProvider?.invoke()?.takeIf { it.isNotBlank() }?.let { put("access_token", it) }
                put("config", JSONObject().apply {
                    put("postgres_changes", postgresChanges)
                })
            })
            put("ref", ref)
        }
        webSocket?.send(joinPayload.toString())
        joinNotificationsChannel()
    }

    private fun joinRoomChannel(roomId: String) {
        val ref = refCounter.getAndIncrement().toString()
        val topic = "realtime:public:room_messages:room_id=eq.$roomId"
        val postgresChanges = org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("event", "*")
                put("schema", "public")
                put("table", "room_messages")
                put("filter", "room_id=eq.$roomId")
            })
            put(JSONObject().apply {
                put("event", "*")
                put("schema", "public")
                put("table", "room_message_reactions")
            })
            put(JSONObject().apply {
                put("event", "*")
                put("schema", "public")
                put("table", "room_members")
                put("filter", "room_id=eq.$roomId")
            })
            put(JSONObject().apply {
                put("event", "*")
                put("schema", "public")
                put("table", "notifications")
            })
        }
        val joinPayload = JSONObject().apply {
            put("topic", topic)
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                accessTokenProvider?.invoke()?.takeIf { it.isNotBlank() }?.let { put("access_token", it) }
                put("config", JSONObject().apply {
                    put("postgres_changes", postgresChanges)
                })
            })
            put("ref", ref)
        }
        webSocket?.send(joinPayload.toString())
        joinNotificationsChannel()
        Log.d(tag, "Joined realtime channel for room $roomId")
    }

    private fun joinNotificationsChannel() {
        val ref = refCounter.getAndIncrement().toString()
        val topic = "realtime:public:notifications"
        val postgresChanges = org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("event", "*")
                put("schema", "public")
                put("table", "notifications")
            })
        }
        val joinPayload = JSONObject().apply {
            put("topic", topic)
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                accessTokenProvider?.invoke()?.takeIf { it.isNotBlank() }?.let { put("access_token", it) }
                put("config", JSONObject().apply { put("postgres_changes", postgresChanges) })
            })
            put("ref", ref)
        }
        webSocket?.send(joinPayload.toString())
    }

    private fun scheduleReconnect() {
        if (!shouldReconnect || reconnectJob?.isActive == true) return
        val delayMs = minOf(30000L, 1000L * (1L shl minOf(reconnectAttempt, 5)))
        reconnectAttempt++
        reconnectJob = coroutineScope.launch {
            delay(delayMs)
            if (shouldReconnect) openSocket()
        }
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = coroutineScope.launch {
            while (isActive) {
                delay(30000)
                try {
                    val heartbeat = JSONObject().apply {
                        put("topic", "phoenix")
                        put("event", "heartbeat")
                        put("payload", JSONObject())
                        put("ref", refCounter.getAndIncrement().toString())
                    }
                    webSocket?.send(heartbeat.toString())
                } catch (e: Exception) {
                    Log.w(tag, "Heartbeat failed: ${e.message}")
                }
            }
        }
    }

    private fun handleMessage(jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            if (json.optString("event") != "postgres_changes") return
            val payload = json.optJSONObject("payload") ?: return
            val data = payload.optJSONObject("data") ?: return
            val table = data.optString("table", "")
            val record = data.optJSONObject("record")
            val oldRecord = data.optJSONObject("old_record")
            val id = record?.optString("id")?.takeIf { it.isNotBlank() }
                ?: oldRecord?.optString("id")?.takeIf { it.isNotBlank() }

            onDataChangedCallback?.invoke(table, id)

            if (table == "notifications") return
            val eventType = data.optString("type", "INSERT")
            if (eventType != "INSERT" || record == null) return

            val roomId = record.optString("room_id", "")
            val conversationId = record.optString("conversation_id", "")
            val senderId = record.optString("sender_id", "")
            val body = record.optString("body", "")
            val kindStr = record.optString("kind", "text")
            val stickerEmoji = record.optJSONObject("metadata")?.optString("sticker_emoji")
            val replyToId = record.optString("reply_to_id").takeIf { it.isNotBlank() }
            val createdAt = record.optString("created_at", "")

            val kind = when (kindStr.lowercase()) {
                "sticker" -> MessageKind.STICKER
                "system" -> MessageKind.SYSTEM
                "media" -> MessageKind.MEDIA
                "reply" -> MessageKind.REPLY
                else -> MessageKind.TEXT
            }

            if (conversationId.isNotBlank() && table == "conversation_messages") {
                onNewDirectMessageCallback?.invoke(
                    com.example.phchat.model.DirectMessage(
                        id = id.orEmpty(),
                        conversationId = conversationId,
                        senderId = senderId,
                        body = body,
                        kind = kind,
                        stickerEmoji = stickerEmoji,
                        timestamp = createdAt
                    )
                )
                return
            }

            if (roomId.isNotBlank() && table == "room_messages") {
                onNewMessageCallback?.invoke(
                    RoomMessage(
                        id = id.orEmpty(),
                        roomId = roomId,
                        senderId = senderId,
                        senderName = "Tambay",
                        senderAvatarHex = 0xFF00A94F,
                        body = body,
                        kind = kind,
                        stickerEmoji = stickerEmoji,
                        replyTo = replyToId?.let { ReplySummary(it, "Kasama", "") },
                        timestamp = "Live"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "Error parsing realtime message: ${e.message}")
        }
    }

    fun disconnect() {
        shouldReconnect = false
        reconnectJob?.cancel()
        reconnectJob = null
        reconnectAttempt = 0
        heartbeatJob?.cancel()
        heartbeatJob = null
        try {
            webSocket?.close(1000, "User left realtime")
        } catch (e: Exception) {
            // Ignored
        }
        webSocket = null
        currentRoomId = null
        currentConversationId = null
        onNewDirectMessageCallback = null
        onNewMessageCallback = null
        onDataChangedCallback = null
    }
}
