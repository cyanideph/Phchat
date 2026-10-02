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

    fun connectAndSubscribeRoom(roomId: String, onNewMessage: (RoomMessage) -> Unit) {
        disconnect()
        currentRoomId = roomId
        currentConversationId = null
        onNewDirectMessageCallback = null
        onNewMessageCallback = onNewMessage

        val wsUrl = "${SupabaseConfig.url.replaceFirst("https://", "wss://")}/realtime/v1/websocket?apikey=$anonKey&vsn=1.0.0"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(tag, "Realtime WebSocket opened for room: $roomId")
                startHeartbeat()
                joinRoomChannel(roomId)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "Realtime WebSocket failure: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(tag, "Realtime WebSocket closed: $reason ($code)")
            }
        })
    }

    fun connectAndSubscribeConversation(
        conversationId: String,
        onNewMessage: (com.example.phchat.model.DirectMessage) -> Unit
    ) {
        disconnect()
        currentConversationId = conversationId
        currentRoomId = null
        onNewDirectMessageCallback = onNewMessage
        onNewMessageCallback = null

        val wsUrl = "${SupabaseConfig.url.replaceFirst("https://", "wss://")}/realtime/v1/websocket?apikey=$anonKey&vsn=1.0.0"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(tag, "Realtime WebSocket opened for conversation: $conversationId")
                startHeartbeat()
                joinConversationChannel(conversationId)
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                handleMessage(text)
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(tag, "Realtime DM WebSocket failure: ${t.message}")
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(tag, "Realtime DM WebSocket closed: $reason ($code)")
            }
        })
    }

    private fun joinConversationChannel(conversationId: String) {
        val ref = refCounter.getAndIncrement().toString()
        val topic = "realtime:public:conversation_messages:conversation_id=eq.$conversationId"
        val joinPayload = JSONObject().apply {
            put("topic", topic)
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                accessTokenProvider?.invoke()?.takeIf { it.isNotBlank() }?.let { put("access_token", it) }
                put("config", JSONObject().apply {
                    put("postgres_changes", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("event", "INSERT")
                            put("schema", "public")
                            put("table", "conversation_messages")
                            put("filter", "conversation_id=eq.$conversationId")
                        })
                    })
                })
            })
            put("ref", ref)
        }
        webSocket?.send(joinPayload.toString())
    }

    private fun joinRoomChannel(roomId: String) {
        val ref = refCounter.getAndIncrement().toString()
        val topic = "realtime:public:room_messages:room_id=eq.$roomId"

        val joinPayload = JSONObject().apply {
            put("topic", topic)
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                accessTokenProvider?.invoke()?.takeIf { it.isNotBlank() }?.let { put("access_token", it) }
                put("config", JSONObject().apply {
                    put("postgres_changes", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("event", "INSERT")
                            put("schema", "public")
                            put("table", "room_messages")
                            put("filter", "room_id=eq.$roomId")
                        })
                    })
                })
            })
            put("ref", ref)
        }

        webSocket?.send(joinPayload.toString())
        Log.d(tag, "Joined realtime channel for room $roomId")
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
            val event = json.optString("event")

            if (event == "postgres_changes") {
                val payload = json.optJSONObject("payload") ?: return
                val data = payload.optJSONObject("data") ?: return
                val record = data.optJSONObject("record") ?: return

                val id = record.optString("id", "")
                val roomId = record.optString("room_id", "")
                val conversationId = record.optString("conversation_id", "")
                val senderId = record.optString("sender_id", "")
                val body = record.optString("body", "")
                val kindStr = record.optString("kind", "text")
                val stickerEmoji = if (record.isNull("sticker_emoji")) null else record.optString("sticker_emoji")
                val replyToId = if (record.isNull("reply_to_id")) null else record.optString("reply_to_id")
                val createdAt = record.optString("created_at", "")

                val kind = when (kindStr.lowercase()) {
                    "sticker" -> MessageKind.STICKER
                    "system" -> MessageKind.SYSTEM
                    "media" -> MessageKind.MEDIA
                    "reply" -> MessageKind.REPLY
                    else -> MessageKind.TEXT
                }

                if (conversationId.isNotBlank()) {
                    val sticker = record.optJSONObject("metadata")?.optString("sticker_emoji")
                    onNewDirectMessageCallback?.invoke(
                        com.example.phchat.model.DirectMessage(
                            id = id,
                            conversationId = conversationId,
                            senderId = senderId,
                            body = body,
                            kind = kind,
                            stickerEmoji = sticker,
                            timestamp = createdAt
                        )
                    )
                    return
                }

                val roomMessage = RoomMessage(
                    id = id,
                    roomId = roomId,
                    senderId = senderId,
                    senderName = if (senderId == "uzzapbot") "uzzapbot" else "Tambay",
                    senderAvatarHex = 0xFF002F6C,
                    body = body,
                    kind = kind,
                    stickerEmoji = stickerEmoji,
                    replyTo = if (replyToId != null) ReplySummary(replyToId, "Kasama", "") else null,
                    timestamp = "Live"
                )

                onNewMessageCallback?.invoke(roomMessage)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error parsing realtime message: ${e.message}")
        }
    }

    fun disconnect() {
        heartbeatJob?.cancel()
        heartbeatJob = null
        try {
            webSocket?.close(1000, "User left room")
        } catch (e: Exception) {
            // Ignored
        }
        webSocket = null
        currentRoomId = null
        currentConversationId = null
        onNewDirectMessageCallback = null
        onNewMessageCallback = null
    }
}
