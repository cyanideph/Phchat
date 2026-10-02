package com.example.phchat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phchat.data.PhchatRepository
import com.example.phchat.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data class Home(val initialTab: Int = 0) : Screen()
    data class RoomChat(val roomId: String) : Screen()
    data class DirectChat(val conversationId: String) : Screen()
    data class ProfileDetail(val profileId: String) : Screen()
}

class PhchatViewModel(
    private val repository: PhchatRepository = PhchatRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home(0))
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    val currentUser = repository.currentUser
    val profiles = repository.profiles
    val rooms = repository.rooms
    val roomMessages = repository.roomMessages
    val conversations = repository.conversations
    val directMessages = repository.directMessages
    val contentPosts = repository.contentPosts
    val profileComments = repository.profileComments
    val profileVisits = repository.profileVisits
    val notifications = repository.notifications
    val hasCheckedInToday = repository.hasCheckedInToday

    // Search and filter state
    val selectedProvinceFilter = MutableStateFlow("ALL")
    val searchQuery = MutableStateFlow("")

    val filteredRooms: StateFlow<List<Room>> = combine(
        rooms,
        selectedProvinceFilter,
        searchQuery
    ) { allRooms, province, query ->
        allRooms.filter { room ->
            val matchesProvince = province == "ALL" || room.provinceCode == province
            val matchesQuery = query.isBlank() ||
                    room.name.contains(query, ignoreCase = true) ||
                    room.announcement.contains(query, ignoreCase = true) ||
                    room.provinceName.contains(query, ignoreCase = true)
            matchesProvince && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        _currentScreen.value = Screen.Home(0)
    }

    fun openRoom(roomId: String) {
        _currentScreen.value = Screen.RoomChat(roomId)
    }

    fun openDirectChat(conversationId: String) {
        _currentScreen.value = Screen.DirectChat(conversationId)
    }

    fun openProfile(profileId: String) {
        repository.recordProfileVisit(profileId)
        _currentScreen.value = Screen.ProfileDetail(profileId)
    }

    fun startConversationWithUser(profile: Profile) {
        val conv = repository.startOrGetConversation(profile)
        _currentScreen.value = Screen.DirectChat(conv.id)
    }

    // Actions delegation
    fun checkIn() = repository.checkInToday()

    fun updateStatus(status: String) = repository.updateMyStatus(status)

    fun sendRoomMessage(roomId: String, body: String, replyTo: ReplySummary? = null) {
        repository.sendRoomMessage(roomId, body, replyTo)
    }

    fun sendRoomSticker(roomId: String, sticker: ChatSticker) {
        repository.sendRoomSticker(roomId, sticker)
    }

    fun toggleMessageReaction(roomId: String, messageId: String, emoji: String) {
        repository.toggleMessageReaction(roomId, messageId, emoji)
    }

    fun deleteMessage(roomId: String, messageId: String) {
        repository.deleteMessage(roomId, messageId)
    }

    fun pinRoomMessage(roomId: String, message: RoomMessage) {
        repository.pinRoomMessage(roomId, message)
    }

    fun toggleRoomPinned(roomId: String) {
        repository.toggleRoomPinned(roomId)
    }

    fun toggleJoinRoom(roomId: String) {
        repository.toggleJoinRoom(roomId)
    }

    fun updateRoomAnnouncement(roomId: String, announcement: String) {
        repository.updateRoomAnnouncement(roomId, announcement)
    }

    fun toggleRoomLock(roomId: String) {
        repository.toggleRoomLock(roomId)
    }

    fun createRoom(name: String, provinceCode: String, provinceName: String, announcement: String): Room {
        return repository.createRoom(name, provinceCode, provinceName, announcement)
    }

    fun sendDirectMessage(conversationId: String, text: String) {
        repository.sendDirectMessage(conversationId, text)
    }

    fun votePoll(postId: String, optionId: String) {
        repository.votePoll(postId, optionId)
    }

    fun togglePostLike(postId: String) {
        repository.togglePostLike(postId)
    }

    fun togglePostSave(postId: String) {
        repository.togglePostSave(postId)
    }

    fun createPost(title: String, body: String, category: String, pollOptions: List<String> = emptyList()) {
        repository.createPost(title, body, category, pollOptions)
    }

    fun addProfileComment(profileId: String, body: String) {
        repository.addProfileComment(profileId, body)
    }

    fun voteProfileComment(profileId: String, commentId: String, delta: Int) {
        repository.voteProfileComment(profileId, commentId, delta)
    }

    fun toggleFollowUser(userId: String) {
        repository.toggleFollowUser(userId)
    }

    fun toggleBlockUser(userId: String) {
        repository.toggleBlockUser(userId)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
    }

    fun clearNotifications() {
        repository.clearNotifications()
    }
}
