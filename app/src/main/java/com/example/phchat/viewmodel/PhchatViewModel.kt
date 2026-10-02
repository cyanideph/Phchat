package com.example.phchat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.phchat.data.AuthState
import com.example.phchat.data.SupabaseAuthManager
import com.example.phchat.data.SupabaseRepository
import com.example.phchat.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    data class Home(val initialTab: Int = 0) : Screen()
    data class RoomChat(val roomId: String) : Screen()
    data class DirectChat(val conversationId: String) : Screen()
    data class ProfileDetail(val profileId: String) : Screen()
    object Auth : Screen()
    object Settings : Screen()
}

class PhchatViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = SupabaseAuthManager(application)
    private val repository = SupabaseRepository(authManager)

    val authState: StateFlow<AuthState> = authManager.authState

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home(0))
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val selectedProvinceFilter = MutableStateFlow("ALL")

    val filteredRooms: StateFlow<List<Room>> = combine(
        _rooms,
        searchQuery,
        selectedProvinceFilter
    ) { roomList, query, province ->
        roomList.filter { room ->
            val matchesQuery = query.isBlank() ||
                room.name.contains(query, ignoreCase = true) ||
                room.announcement.contains(query, ignoreCase = true) ||
                room.provinceCode.contains(query, ignoreCase = true)
            val matchesProvince = province == "ALL" || room.provinceCode.equals(province, ignoreCase = true)
            matchesQuery && matchesProvince
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _roomMessages = MutableStateFlow<Map<String, List<RoomMessage>>>(emptyMap())
    val roomMessages: StateFlow<Map<String, List<RoomMessage>>> = _roomMessages.asStateFlow()

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val realtimeClient = com.example.phchat.data.SupabaseRealtimeClient(accessTokenProvider = { authManager.getAccessToken() })
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    private val _buddies = MutableStateFlow<List<Profile>>(emptyList())
    val buddies: StateFlow<List<Profile>> = _buddies.asStateFlow()

    private val _directMessages = MutableStateFlow<Map<String, List<DirectMessage>>>(emptyMap())
    val directMessages: StateFlow<Map<String, List<DirectMessage>>> = _directMessages.asStateFlow()

    private val _communityPosts = MutableStateFlow<List<ContentPost>>(emptyList())
    val communityPosts: StateFlow<List<ContentPost>> = _communityPosts.asStateFlow()
    val contentPosts: StateFlow<List<ContentPost>> = _communityPosts.asStateFlow()

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _profileVisits = MutableStateFlow<List<ProfileVisit>>(emptyList())
    val profileVisits: StateFlow<List<ProfileVisit>> = _profileVisits.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _notificationPreferences = MutableStateFlow(NotificationPreferences())
    val notificationPreferences: StateFlow<NotificationPreferences> = _notificationPreferences.asStateFlow()

    private val _profileComments = MutableStateFlow<Map<String, List<ProfileComment>>>(emptyMap())
    val profileComments: StateFlow<Map<String, List<ProfileComment>>> = _profileComments.asStateFlow()

    private val _currentUser = MutableStateFlow(
        Profile(
            id = authManager.getCurrentUserId() ?: "guest_user",
            username = "guest",
            displayName = "Tambay",
            avatarInitial = "T",
            avatarColorHex = 0xFF0038A8,
            bio = "Active Tambay",
            statusText = "Online",
            province = "NCR",
            isActive = true
        )
    )
    val currentUser: StateFlow<Profile> = _currentUser.asStateFlow()

    val hasCheckedInToday = MutableStateFlow(false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            authManager.authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        _currentUser.value = Profile(
                            id = state.user.id,
                            username = state.user.username.ifBlank { state.user.email.substringBefore("@") },
                            displayName = state.user.displayName.ifBlank { state.user.email.substringBefore("@") },
                            avatarInitial = state.user.displayName.take(1).uppercase(),
                            avatarColorHex = 0xFF0038A8,
                            bio = "Mabuhay!",
                            statusText = "Online sa Supabase",
                            province = "NCR",
                            isActive = true
                        )
                        loadSupabaseData()
                        if (_currentScreen.value is Screen.Auth) {
                            _currentScreen.value = Screen.Home(0)
                        }
                    }
                    is AuthState.Unauthenticated -> {
                        if (_currentScreen.value !is Screen.Auth) {
                            _currentScreen.value = Screen.Auth
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    fun loadSupabaseData() {
        viewModelScope.launch {
            _isLoading.value = true

            val roomsResult = repository.getRooms()
            if (roomsResult.isSuccess) {
                _rooms.value = roomsResult.getOrNull().orEmpty()
            }

            val profilesResult = repository.getProfiles()
            if (profilesResult.isSuccess) {
                val list = profilesResult.getOrNull().orEmpty()
                _profiles.value = list
                _profileVisits.value = list.take(5).map {
                    ProfileVisit(
                        id = "visit_${it.id}",
                        profileId = _currentUser.value.id,
                        visitor = it
                    )
                }
            }

            val contentsResult = repository.getContents()
            if (contentsResult.isSuccess) _communityPosts.value = contentsResult.getOrNull().orEmpty()

            val conversationsResult = repository.getConversations()
            if (conversationsResult.isSuccess) _conversations.value = conversationsResult.getOrNull().orEmpty()

            val notificationsResult = repository.getNotifications()
            if (notificationsResult.isSuccess) _notifications.value = notificationsResult.getOrNull().orEmpty()

            _isLoading.value = false
        }
    }

    fun signIn(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = authManager.signIn(email, pass)
            if (res.isSuccess) {
                onResult(true, "Maligayang pagbabalik!")
            } else {
                onResult(false, res.exceptionOrNull()?.localizedMessage ?: "Sign in failed")
            }
        }
    }

    fun signUp(email: String, password: String, username: String, displayName: String, province: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val res = authManager.signUp(email, password, username, displayName, province)
            onResult(res.getOrNull() ?: res.exceptionOrNull()?.localizedMessage ?: "Sign up error")
        }
    }

    fun signOut() {
        authManager.signOut()
        _currentScreen.value = Screen.Auth
    }

    fun openAuthScreen() {
        navigateTo(Screen.Auth)
    }

    fun navigateTo(screen: Screen) {
        screenBackStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        realtimeClient.disconnect()
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
        } else {
            _currentScreen.value = Screen.Home(0)
        }
    }

    fun openRoom(roomId: String) {
        navigateTo(Screen.RoomChat(roomId))
        loadRoomMessages(roomId)
        realtimeClient.connectAndSubscribeRoom(roomId) { newMsg ->
            viewModelScope.launch(Dispatchers.Main) {
                if (newMsg.senderId in _blockedUserIds.value) return@launch
                val currentMap = _roomMessages.value.toMutableMap()
                val list = (currentMap[roomId] ?: emptyList()).toMutableList()
                if (list.none { it.id == newMsg.id }) {
                    list.add(newMsg)
                    currentMap[roomId] = list
                    _roomMessages.value = currentMap
                }
            }
        }
    }

    fun submitReport(targetId: String, reason: String, details: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val res = repository.submitReport(targetId, reason, details)
            onResult(res.isSuccess)
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            val set = _blockedUserIds.value.toMutableSet()
            set.add(userId)
            _blockedUserIds.value = set
            repository.blockUser(userId)
        }
    }

    fun addBuddy(targetUserId: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.addBuddy(targetUserId)
            if (res.isSuccess) {
                onResult(true, "Matagumpay na naidagdag sa Barkada!")
            } else {
                onResult(false, res.exceptionOrNull()?.localizedMessage ?: "Hindi ma-add sa barkada")
            }
        }
    }

    fun sendKalabit(roomId: String) {
        val user = _currentUser.value
        sendRoomMessage(
            roomId = roomId,
            body = "📳 KALABIT! Nag-buzz si ${user.displayName} sa buong tambayan!",
            kind = MessageKind.SYSTEM
        )
    }

    fun openDirectChat(conversationId: String) {
        navigateTo(Screen.DirectChat(conversationId))
        loadDirectMessages(conversationId)
        realtimeClient.connectAndSubscribeConversation(conversationId) { newMsg ->
            viewModelScope.launch(Dispatchers.Main) {
                if (newMsg.senderId in _blockedUserIds.value) return@launch
                val currentMap = _directMessages.value.toMutableMap()
                val list = (currentMap[conversationId] ?: emptyList()).toMutableList()
                if (list.none { it.id == newMsg.id }) {
                    list.add(newMsg)
                    currentMap[conversationId] = list
                    _directMessages.value = currentMap
                }
            }
        }
    }

    fun loadDirectMessages(conversationId: String) {
        viewModelScope.launch {
            val res = repository.getDirectMessages(conversationId)
            if (res.isSuccess) {
                val map = _directMessages.value.toMutableMap()
                map[conversationId] = res.getOrNull().orEmpty()
                _directMessages.value = map
            }
        }
    }

    fun openProfile(profileId: String) {
        navigateTo(Screen.ProfileDetail(profileId))
    }

    fun filterRoomsByProvince(code: String) {
        selectedProvinceFilter.value = code
    }

    fun loadRoomMessages(roomId: String) {
        viewModelScope.launch {
            val res = repository.getRoomMessages(roomId)
            if (res.isSuccess) {
                val list = res.getOrNull().orEmpty()
                val map = _roomMessages.value.toMutableMap()
                map[roomId] = list
                _roomMessages.value = map
            }
        }
    }

    fun sendRoomMessage(roomId: String, body: String, replyTo: ReplySummary?) {
        sendRoomMessage(
            roomId = roomId,
            body = body,
            kind = MessageKind.TEXT,
            stickerEmoji = null,
            replyTo = replyTo
        )
    }

    fun sendRoomMessage(
        roomId: String,
        body: String,
        kind: MessageKind = MessageKind.TEXT,
        stickerEmoji: String? = null,
        replyTo: ReplySummary? = null
    ) {
        viewModelScope.launch {
            val me = _currentUser.value
            val tempMsg = RoomMessage(
                id = "temp_${System.currentTimeMillis()}",
                roomId = roomId,
                senderId = me.id,
                senderName = me.displayName,
                senderAvatarHex = me.avatarColorHex,
                body = body,
                kind = kind,
                stickerEmoji = stickerEmoji,
                replyTo = replyTo,
                timestamp = "Just now"
            )

            val currentMap = _roomMessages.value.toMutableMap()
            val list = (currentMap[roomId] ?: emptyList()).toMutableList()
            list.add(tempMsg)
            currentMap[roomId] = list
            _roomMessages.value = currentMap

            val kindStr = when (kind) {
                MessageKind.STICKER -> "sticker"
                MessageKind.SYSTEM -> "system"
                else -> "text"
            }
            val result = repository.sendRoomMessage(roomId, body, kindStr, replyTo?.id)
            if (result.isFailure) {
                val map = _roomMessages.value.toMutableMap()
                map[roomId] = (map[roomId] ?: emptyList()).filterNot { it.id == tempMsg.id }
                _roomMessages.value = map
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage
            } else loadRoomMessages(roomId)
        }
    }

    fun sendRoomSticker(roomId: String, sticker: ChatSticker) {
        sendRoomMessage(
            roomId = roomId,
            body = sticker.tagalogPhrase,
            kind = MessageKind.STICKER,
            stickerEmoji = sticker.emoji
        )
    }

    fun deleteMessage(roomId: String, messageId: String) {
        val currentMap = _roomMessages.value.toMutableMap()
        val list = (currentMap[roomId] ?: emptyList()).filter { it.id != messageId }
        currentMap[roomId] = list
        _roomMessages.value = currentMap
    }

    fun toggleMessageReaction(roomId: String, messageId: String, emoji: String) {
        val currentMap = _roomMessages.value.toMutableMap()
        val list = (currentMap[roomId] ?: emptyList()).map { msg ->
            if (msg.id == messageId) {
                val currentCount = msg.reactions[emoji] ?: 0
                val updatedReactions = msg.reactions.toMutableMap()
                if (msg.myReaction == emoji) {
                    if (currentCount > 1) updatedReactions[emoji] = currentCount - 1 else updatedReactions.remove(emoji)
                    msg.copy(reactions = updatedReactions, myReaction = null)
                } else {
                    updatedReactions[emoji] = currentCount + 1
                    msg.copy(reactions = updatedReactions, myReaction = emoji)
                }
            } else msg
        }
        currentMap[roomId] = list
        _roomMessages.value = currentMap
    }

    fun toggleRoomLock(roomId: String) {
        val room = _rooms.value.firstOrNull { it.id == roomId } ?: return
        val locked = !room.isLocked
        _rooms.value = _rooms.value.map { if (it.id == roomId) it.copy(isLocked = locked) else it }
        viewModelScope.launch {
            val result = repository.updateRoom(roomId, org.json.JSONObject().apply { put("is_locked", locked) })
            if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
        }
    }

    fun updateRoomAnnouncement(roomId: String, announcement: String) {
        _rooms.value = _rooms.value.map { r -> if (r.id == roomId) r.copy(announcement = announcement) else r }
        viewModelScope.launch {
            val result = repository.updateRoom(roomId, org.json.JSONObject().apply { put("announcement", announcement) })
            if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
        }
    }

    fun toggleRoomPinned(roomId: String) {
        val room = _rooms.value.firstOrNull { it.id == roomId } ?: return
        val pinned = !room.isPinned
        _rooms.value = _rooms.value.map { r -> if (r.id == roomId) r.copy(isPinned = pinned) else r }
        viewModelScope.launch {
            val result = repository.setRoomPinned(roomId, pinned)
            if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
        }
    }

    fun toggleJoinRoom(roomId: String) {
        val room = _rooms.value.firstOrNull { it.id == roomId } ?: return
        val newJoined = !room.isJoined
        _rooms.value = _rooms.value.map { r ->
            if (r.id == roomId) r.copy(isJoined = newJoined, memberCount = if (newJoined) r.memberCount + 1 else (r.memberCount - 1).coerceAtLeast(0)) else r
        }
        viewModelScope.launch {
            val result = repository.toggleRoomMembership(roomId)
            if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
        }
    }

    fun pinRoomMessage(roomId: String, message: RoomMessage) {
        _rooms.value = _rooms.value.map { r ->
            if (r.id == roomId) r.copy(pinnedMessage = message) else r
        }
    }

    fun createRoom(name: String, provinceCode: String, provinceName: String, announcement: String): Room {
        val newRoom = Room(
            id = "room_${System.currentTimeMillis()}",
            name = name,
            slug = name.lowercase().replace(" ", "-"),
            provinceCode = provinceCode,
            provinceName = provinceName,
            announcement = announcement,
            memberCount = 1,
            onlineCount = 1
        )
        _rooms.value = listOf(newRoom) + _rooms.value
        viewModelScope.launch {
            repository.createRoom(name, provinceCode, announcement)
        }
        return newRoom
    }

    fun sendDirectMessage(conversationId: String, body: String) {
        val trimmed = body.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            val result = repository.sendDirectMessage(conversationId, trimmed)
            if (result.isSuccess) {
                val msg = result.getOrNull() ?: return@launch
                val currentMap = _directMessages.value.toMutableMap()
                val list = (currentMap[conversationId] ?: emptyList()).toMutableList()
                if (list.none { it.id == msg.id }) {
                    list.add(msg)
                    currentMap[conversationId] = list
                    _directMessages.value = currentMap
                }
            } else {
                _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to send message"
            }
        }
    }

    fun startConversationWithUser(user: Profile) {
        viewModelScope.launch {
            val existing = _conversations.value.firstOrNull { it.participant.id == user.id }
            val conversationId = if (existing != null) {
                existing.id
            } else {
                val result = repository.createDirectConversation(user.id)
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.localizedMessage ?: "Failed to create conversation"
                    return@launch
                }
                result.getOrNull() ?: return@launch
            }
            val refreshed = repository.getConversations()
            if (refreshed.isSuccess) {
                _conversations.value = refreshed.getOrNull().orEmpty()
            }
            openDirectChat(conversationId)
        }
    }

    fun createPost(title: String, body: String, categoryName: String, pollOptions: List<String>) {
        val poll = if (pollOptions.isNotEmpty()) {
            PollData(
                id = "poll_${System.currentTimeMillis()}",
                question = title,
                options = pollOptions.mapIndexed { idx, opt ->
                    PollOption(id = "opt_$idx", text = opt, votes = 0)
                },
                totalVotes = 0
            )
        } else null

        val post = ContentPost(
            id = "post_${System.currentTimeMillis()}",
            author = _currentUser.value,
            category = categoryName,
            title = title,
            body = body,
            createdAt = "Just now",
            poll = poll
        )
        _communityPosts.value = listOf(post) + _communityPosts.value
        viewModelScope.launch {
            val result = repository.createContent(title, body)
            if (result.isSuccess) loadSupabaseData()
            else _errorMessage.value = result.exceptionOrNull()?.localizedMessage
        }
    }

    fun votePoll(postId: String, optionId: String) {
        val posts = _communityPosts.value.toMutableList()
        val index = posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = posts[index]
            val poll = post.poll ?: return
            if (poll.hasVoted) return

            val updatedOptions = poll.options.map { opt ->
                if (opt.id == optionId) opt.copy(votes = opt.votes + 1) else opt
            }
            val updatedPoll = poll.copy(
                options = updatedOptions,
                totalVotes = poll.totalVotes + 1,
                hasVoted = true,
                selectedOptionId = optionId
            )
            posts[index] = post.copy(poll = updatedPoll)
            _communityPosts.value = posts
            viewModelScope.launch {
                val result = repository.votePoll(postId, optionId)
                if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
            }
        }
    }

    fun togglePostLike(postId: String) {
        val posts = _communityPosts.value.toMutableList()
        val index = posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = posts[index]
            val newLiked = !post.isLiked
            val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
            posts[index] = post.copy(isLiked = newLiked, likesCount = newCount)
            _communityPosts.value = posts
            viewModelScope.launch {
                val result = repository.toggleContentReaction(postId)
                if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
            }
        }
    }

    fun togglePostSave(postId: String) {
        val posts = _communityPosts.value.toMutableList()
        val index = posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val post = posts[index]
            posts[index] = post.copy(isSaved = !post.isSaved)
            _communityPosts.value = posts
            viewModelScope.launch {
                val result = repository.toggleContentSave(postId)
                if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
            }
        }
    }

    fun checkIn() {
        if (hasCheckedInToday.value) return
        viewModelScope.launch {
            val result = repository.checkInToday()
            if (result.isSuccess && result.getOrNull() == true) {
                hasCheckedInToday.value = true
                val user = _currentUser.value
                _currentUser.value = user.copy(streak = user.streak + 1, points = user.points + 50)
            } else if (result.isFailure) _errorMessage.value = result.exceptionOrNull()?.localizedMessage
        }
    }

    fun updateStatus(status: String) {
        val u = _currentUser.value
        _currentUser.value = u.copy(statusText = status)
    }

    fun updateProfile(displayName: String, statusText: String, bio: String, province: String) {
        val u = _currentUser.value
        _currentUser.value = u.copy(displayName = displayName, statusText = statusText, bio = bio, province = province)
        viewModelScope.launch {
            val result = repository.updateProfile(displayName, statusText, bio)
            if (result.isFailure) _errorMessage.value = result.exceptionOrNull()?.localizedMessage
        }
    }

    fun toggleFollowUser(userId: String) {
        _profiles.value = _profiles.value.map { p ->
            if (p.id == userId) p.copy(isFollowed = !p.isFollowed) else p
        }
        viewModelScope.launch {
            val result = repository.toggleFollow(userId)
            if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
        }
    }

    fun toggleBlockUser(userId: String) {
        _profiles.value = _profiles.value.map { p ->
            if (p.id == userId) p.copy(isBlocked = !p.isBlocked) else p
        }
        viewModelScope.launch {
            val result = repository.toggleBlock(userId)
            if (result.isFailure) { loadSupabaseData(); _errorMessage.value = result.exceptionOrNull()?.localizedMessage }
        }
    }

    fun addProfileComment(profileId: String, body: String) {
        val currentComments = _profileComments.value.toMutableMap()
        val list = (currentComments[profileId] ?: emptyList()).toMutableList()
        val newComment = ProfileComment(
            id = "comment_${System.currentTimeMillis()}",
            profileId = profileId,
            author = _currentUser.value,
            body = body,
            createdAt = "Just now"
        )
        list.add(0, newComment)
        currentComments[profileId] = list
        _profileComments.value = currentComments
        viewModelScope.launch {
            val result = repository.addProfileComment(profileId, body)
            if (result.isFailure) _errorMessage.value = result.exceptionOrNull()?.localizedMessage
        }
    }

    fun voteProfileComment(profileId: String, commentId: String, delta: Int) {
        val currentComments = _profileComments.value.toMutableMap()
        val list = (currentComments[profileId] ?: emptyList()).map { c ->
            if (c.id == commentId) {
                val newVote = if (c.userVote == delta) 0 else delta
                val voteDiff = newVote - c.userVote
                c.copy(votes = c.votes + voteDiff, userVote = newVote)
            } else c
        }
        currentComments[profileId] = list
        _profileComments.value = currentComments
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            val result = repository.markNotificationsRead()
            if (result.isSuccess) _notifications.value = _notifications.value.map { it.copy(isRead = true) }
            else _errorMessage.value = result.exceptionOrNull()?.localizedMessage
        }
    }

    fun loadNotificationPreferences() {
        viewModelScope.launch {
            val result = repository.getNotificationPreferences()
            if (result.isSuccess) {
                _notificationPreferences.value = result.getOrNull() ?: NotificationPreferences()
            }
        }
    }

    fun updateNotificationPreference(column: String, enabled: Boolean) {
        viewModelScope.launch {
            val result = repository.updateNotificationPreference(column, enabled)
            if (result.isSuccess) {
                loadNotificationPreferences()
            }
        }
    }

}