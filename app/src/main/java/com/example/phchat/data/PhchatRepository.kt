package com.example.phchat.data

import com.example.phchat.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PhchatRepository {

    private fun currentTimeString(): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }

    private fun currentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // Current User
    private val _currentUser = MutableStateFlow(
        Profile(
            id = "usr_me",
            username = "juan_tamad",
            displayName = "Juan dela Cruz",
            avatarInitial = "J",
            avatarColorHex = 0xFF0038A8,
            bio = "Proud Pinoy 🇵🇭 | Tambay ng Manila Central | Mahilig sa kape at OPM",
            statusText = "Online & Tambay ☕",
            province = "Metro Manila",
            isActive = true,
            lastSeenAt = "Just now",
            points = 450,
            streak = 5,
            isFeatured = true
        )
    )
    val currentUser: StateFlow<Profile> = _currentUser.asStateFlow()

    // Community Profiles
    private val _profiles = MutableStateFlow(
        listOf(
            _currentUser.value,
            Profile(
                id = "usr_maria",
                username = "maria_clara",
                displayName = "Maria Clara 🌺",
                avatarInitial = "M",
                avatarColorHex = 0xFFCE1126,
                bio = "Cebuana vibes | Traveler & food lover | Sugbo Tambayan Host",
                statusText = "Eating Lechon sa Sugbo 🐷",
                province = "Cebu",
                isActive = true,
                lastSeenAt = "Online",
                points = 820,
                streak = 14,
                isFeatured = true,
                isFollowed = true
            ),
            Profile(
                id = "usr_davao",
                username = "davao_eagle",
                displayName = "Davao Idol 🦅",
                avatarInitial = "D",
                avatarColorHex = 0xFF059669,
                bio = "Durian country represent! Peace and good vibes only.",
                statusText = "Tara tambay!",
                province = "Davao",
                isActive = true,
                lastSeenAt = "5m ago",
                points = 640,
                streak = 9,
                isFeatured = true
            ),
            Profile(
                id = "usr_techie",
                username = "pinoy_dev",
                displayName = "Kuya Kevin (Dev)",
                avatarInitial = "K",
                avatarColorHex = 0xFF2563EB,
                bio = "Kotlin & Android builder | Pinoy Tech Chat Admin",
                statusText = "Compiling code 💻",
                province = "Pampanga",
                isActive = false,
                lastSeenAt = "15m ago",
                points = 1100,
                streak = 21,
                isFeatured = true,
                isFollowed = true
            ),
            Profile(
                id = "usr_chika",
                username = "ate_marites",
                displayName = "Ate Marites ✨",
                avatarInitial = "A",
                avatarColorHex = 0xFFD97706,
                bio = "Certified updated sa lahat ng balita sa barangay. Walang sikreto!",
                statusText = "May bagong chismis! 👀",
                province = "Metro Manila",
                isActive = true,
                lastSeenAt = "Online",
                points = 320,
                streak = 3
            )
        )
    )
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    // Rooms (Philippine Provincial and Thematic Lounges)
    private val _rooms = MutableStateFlow(
        listOf(
            Room(
                id = "room_ncr",
                name = "🇵🇭 Manila Central Lounge",
                slug = "manila-central",
                kind = RoomKind.PUBLIC,
                provinceCode = "NCR",
                provinceName = "Metro Manila",
                announcement = "Mabuhay! Welcome sa pambansang tambayan ng Metro Manila. Respect everyone, bawal ang bastos!",
                memberCount = 1420,
                onlineCount = 89,
                isJoined = true,
                isPinned = true,
                myRole = MemberRole.ADMIN,
                colorHex = 0xFF0038A8
            ),
            Room(
                id = "room_cebu",
                name = "🌊 Sugbo Tambayan (Cebu)",
                slug = "sugbo-tambayan",
                kind = RoomKind.PUBLIC,
                provinceCode = "CEB",
                provinceName = "Cebu",
                announcement = "Maayong adlaw mga Bisdak! Tara kape ta o kaon lechon.",
                memberCount = 890,
                onlineCount = 45,
                isJoined = true,
                isPinned = true,
                myRole = MemberRole.MEMBER,
                colorHex = 0xFF0D9488
            ),
            Room(
                id = "room_davao",
                name = "🦅 Davao Eagle's Hub",
                slug = "davao-hub",
                kind = RoomKind.PUBLIC,
                provinceCode = "DVO",
                provinceName = "Davao",
                announcement = "Madayaw Davao! Welcome to Mindanao's premier hangout lounge.",
                memberCount = 560,
                onlineCount = 32,
                isJoined = true,
                isPinned = false,
                myRole = MemberRole.MEMBER,
                colorHex = 0xFF059669
            ),
            Room(
                id = "room_pampanga",
                name = "🍲 Kapampangan Food & Hangout",
                slug = "kapampangan-food",
                kind = RoomKind.PUBLIC,
                provinceCode = "PAM",
                provinceName = "Pampanga",
                announcement = "Manyaman keni! Tara pag-usapan ang pinakamasarap na Sisig at Delicacies.",
                memberCount = 410,
                onlineCount = 28,
                isJoined = false,
                isPinned = false,
                myRole = MemberRole.MEMBER,
                colorHex = 0xFFCE1126
            ),
            Room(
                id = "room_tech",
                name = "💻 Pinoy Devs & Tech Startups",
                slug = "pinoy-tech",
                kind = RoomKind.GROUP,
                provinceCode = "ALL",
                provinceName = "All Philippines",
                announcement = "Tambayan ng mga programmers, designers, at founders sa Pinas. Share projects & tips!",
                memberCount = 680,
                onlineCount = 52,
                isJoined = true,
                isPinned = false,
                myRole = MemberRole.MODERATOR,
                colorHex = 0xFF2563EB
            ),
            Room(
                id = "room_gaming",
                name = "🎮 Pinoy Mobile Gamers (ML / WR)",
                slug = "pinoy-gamers",
                kind = RoomKind.PUBLIC,
                provinceCode = "ALL",
                provinceName = "All Philippines",
                announcement = "LF 5-man squad Mythical Glory! Drop your IGNs below.",
                memberCount = 1120,
                onlineCount = 94,
                isJoined = false,
                isPinned = false,
                myRole = MemberRole.MEMBER,
                colorHex = 0xFF7C3AED
            ),
            Room(
                id = "room_opm",
                name = "🎵 OPM & Acoustic Tambayan",
                slug = "opm-tambayan",
                kind = RoomKind.PUBLIC,
                provinceCode = "ALL",
                provinceName = "All Philippines",
                announcement = "Kantahan at tugtugan session! Share your favorite songs and lyrics.",
                memberCount = 740,
                onlineCount = 38,
                isJoined = true,
                isPinned = false,
                myRole = MemberRole.MEMBER,
                colorHex = 0xFFEA580C
            )
        )
    )
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    // Room Messages keyed by roomId
    private val _roomMessages = MutableStateFlow<Map<String, List<RoomMessage>>>(
        mapOf(
            "room_ncr" to listOf(
                RoomMessage(
                    id = "msg_1",
                    roomId = "room_ncr",
                    senderId = "usr_system",
                    senderName = "Phchat System",
                    senderAvatarHex = 0xFF475569,
                    senderRole = MemberRole.ADMIN,
                    body = "🇵🇭 Welcome to Manila Central Lounge! Bawal ang bastos. Have fun!",
                    kind = MessageKind.SYSTEM,
                    timestamp = "10:00 AM"
                ),
                RoomMessage(
                    id = "msg_2",
                    roomId = "room_ncr",
                    senderId = "usr_maria",
                    senderName = "Maria Clara 🌺",
                    senderAvatarHex = 0xFFCE1126,
                    senderRole = MemberRole.MEMBER,
                    body = "Maayong buntag sa tanan! Kumusta ang trapik sa EDSA ngayon? Haha!",
                    kind = MessageKind.TEXT,
                    reactions = mapOf("😂" to 4, "❤️" to 2),
                    timestamp = "10:14 AM"
                ),
                RoomMessage(
                    id = "msg_3",
                    roomId = "room_ncr",
                    senderId = "usr_me",
                    senderName = "Juan dela Cruz",
                    senderAvatarHex = 0xFF0038A8,
                    senderRole = MemberRole.ADMIN,
                    body = "Normal na EDSA parking lot na naman @maria_clara! Buti na lang naka-tambay sa Phchat.",
                    kind = MessageKind.TEXT,
                    mentions = listOf("maria_clara"),
                    reactions = mapOf("👍" to 3, "☕" to 5),
                    timestamp = "10:15 AM"
                ),
                RoomMessage(
                    id = "msg_4",
                    roomId = "room_ncr",
                    senderId = "usr_chika",
                    senderName = "Ate Marites ✨",
                    senderAvatarHex = 0xFFD97706,
                    senderRole = MemberRole.MEMBER,
                    body = "Tara G! Kape muna tayo sa kanto habang naghihintay ng sahod.",
                    kind = MessageKind.STICKER,
                    stickerTitle = "Kape",
                    stickerEmoji = "☕",
                    reactions = mapOf("☕" to 6, "❤️" to 3),
                    timestamp = "10:18 AM"
                )
            ),
            "room_cebu" to listOf(
                RoomMessage(
                    id = "msg_c1",
                    roomId = "room_cebu",
                    senderId = "usr_maria",
                    senderName = "Maria Clara 🌺",
                    senderAvatarHex = 0xFFCE1126,
                    senderRole = MemberRole.OWNER,
                    body = "Welcome sa Cebu room! Pwede mo-share diri ug mga lamiang kan-anan sa Sugbo.",
                    kind = MessageKind.TEXT,
                    reactions = mapOf("❤️" to 5, "🇵🇭" to 2),
                    timestamp = "9:30 AM"
                )
            ),
            "room_tech" to listOf(
                RoomMessage(
                    id = "msg_t1",
                    roomId = "room_tech",
                    senderId = "usr_techie",
                    senderName = "Kuya Kevin (Dev)",
                    senderAvatarHex = 0xFF2563EB,
                    senderRole = MemberRole.ADMIN,
                    body = "Guys, sino na nakasubok ng Kotlin 2.2 + Jetpack Compose? Ang bilis ng recomposition!",
                    kind = MessageKind.TEXT,
                    reactions = mapOf("🔥" to 7, "🚀" to 4),
                    timestamp = "11:00 AM"
                )
            )
        )
    )
    val roomMessages: StateFlow<Map<String, List<RoomMessage>>> = _roomMessages.asStateFlow()

    // Direct Conversations
    private val _conversations = MutableStateFlow(
        listOf(
            Conversation(
                id = "conv_maria",
                participant = _profiles.value.first { it.id == "usr_maria" },
                lastMessage = "Sige kuya, kita-kits sa tambayan mamaya!",
                lastMessageTime = "10:25 AM",
                unreadCount = 1,
                isPinned = true
            ),
            Conversation(
                id = "conv_techie",
                participant = _profiles.value.first { it.id == "usr_techie" },
                lastMessage = "Sent you the code snippet for the room real-time flow.",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isPinned = false
            ),
            Conversation(
                id = "conv_davao",
                participant = _profiles.value.first { it.id == "usr_davao" },
                lastMessage = "Padala ako ng Durian candy pag-uwi ko dyan!",
                lastMessageTime = "Oct 1",
                unreadCount = 0,
                isPinned = false
            )
        )
    )
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Direct Messages keyed by conversationId
    private val _directMessages = MutableStateFlow<Map<String, List<DirectMessage>>>(
        mapOf(
            "conv_maria" to listOf(
                DirectMessage(
                    id = "dm_1",
                    conversationId = "conv_maria",
                    senderId = "usr_maria",
                    body = "Kumusta Juan! Ikaw ba yung nag-host kanina sa Manila room?",
                    timestamp = "10:20 AM"
                ),
                DirectMessage(
                    id = "dm_2",
                    conversationId = "conv_maria",
                    senderId = "usr_me",
                    body = "Oo Maria! Dami nag-online today galing Cebu at Davao.",
                    timestamp = "10:22 AM"
                ),
                DirectMessage(
                    id = "dm_3",
                    conversationId = "conv_maria",
                    senderId = "usr_maria",
                    body = "Sige kuya, kita-kits sa tambayan mamaya!",
                    kind = MessageKind.STICKER,
                    stickerEmoji = "🛵",
                    timestamp = "10:25 AM"
                )
            )
        )
    )
    val directMessages: StateFlow<Map<String, List<DirectMessage>>> = _directMessages.asStateFlow()

    // Community Content (Posts, Polls, Announcements)
    private val _contentPosts = MutableStateFlow(
        listOf(
            ContentPost(
                id = "post_1",
                author = _profiles.value.first { it.id == "usr_chika" },
                title = "Aling Street Food ang Pinaka-Go-To niyo pag Hapon? 🍢",
                body = "Kwentuhan tayo mga ka-tambay! Pag alas-kwatro na ng hapon sa Pinas, anong una mong hahanapin sa kanto? Isaw, fishball, kwek-kwek, o calamares?",
                category = "Food & Travel",
                kind = ContentKind.POLL,
                poll = PollData(
                    id = "poll_1",
                    question = "Paboritong Pinoy Tusok-Tusok sa Hapon:",
                    options = listOf(
                        PollOption("opt_1", "Kwek-Kwek na maanghang sauce 🥚", 68),
                        PollOption("opt_2", "Inihaw na Isaw & Betamax 🍢", 92),
                        PollOption("opt_3", "Fishball at Kikiam 🍢", 45),
                        PollOption("opt_4", "Turon na may Langka 🍌", 31)
                    ),
                    totalVotes = 236
                ),
                likesCount = 42,
                commentsCount = 18,
                createdAt = "1h ago"
            ),
            ContentPost(
                id = "post_2",
                author = _profiles.value.first { it.id == "usr_techie" },
                title = "Phchat Android App Architecture Guide 🇵🇭",
                body = "Shoutout sa lahat ng Pinoy developers! Ang Phchat ay pure Kotlin + Jetpack Compose with Material 3. Seamless provincial chat rooms, real-time message threading, at daily streak check-in system. Check out our public rooms!",
                category = "General",
                kind = ContentKind.POST,
                likesCount = 56,
                isLiked = true,
                commentsCount = 8,
                createdAt = "3h ago"
            ),
            ContentPost(
                id = "post_3",
                author = _profiles.value.first { it.id == "usr_maria" },
                title = "Top 5 Secret Beaches in Cebu for 2026 🌊",
                body = "Kung magbabakasyon kayo sa Sugbo this year, huwag lang puro Bantayan! Bisitahin din niyo ang mga tagong paraiso sa Tuburan at Lambug. Feel free to PM me for travel tips!",
                category = "Provincial Buzz",
                kind = ContentKind.POST,
                likesCount = 89,
                isSaved = true,
                commentsCount = 24,
                createdAt = "5h ago"
            )
        )
    )
    val contentPosts: StateFlow<List<ContentPost>> = _contentPosts.asStateFlow()

    // Profile Comments / Guestbook Wall
    private val _profileComments = MutableStateFlow<Map<String, List<ProfileComment>>>(
        mapOf(
            "usr_me" to listOf(
                ProfileComment(
                    id = "wall_1",
                    profileId = "usr_me",
                    author = _profiles.value.first { it.id == "usr_maria" },
                    body = "Thanks for the warm welcome sa Manila room lodi! Solid tambayan!",
                    votes = 5,
                    userVote = 1,
                    createdAt = "Yesterday"
                ),
                ProfileComment(
                    id = "wall_2",
                    profileId = "usr_me",
                    author = _profiles.value.first { it.id == "usr_techie" },
                    body = "Petmalu Juan! Keep the community clean and active 🇵🇭",
                    votes = 3,
                    userVote = 0,
                    createdAt = "2 days ago"
                )
            ),
            "usr_maria" to listOf(
                ProfileComment(
                    id = "wall_m1",
                    profileId = "usr_maria",
                    author = _currentUser.value,
                    body = "Sugbo pride! Salamat sa pag-guide sa Cebu tambayan!",
                    votes = 4,
                    userVote = 1,
                    createdAt = "Oct 1"
                )
            )
        )
    )
    val profileComments: StateFlow<Map<String, List<ProfileComment>>> = _profileComments.asStateFlow()

    // Profile Visits History
    private val _profileVisits = MutableStateFlow<Map<String, List<ProfileVisit>>>(
        mapOf(
            "usr_me" to listOf(
                ProfileVisit("v_1", "usr_me", _profiles.value.first { it.id == "usr_maria" }, "10m ago"),
                ProfileVisit("v_2", "usr_me", _profiles.value.first { it.id == "usr_techie" }, "1h ago"),
                ProfileVisit("v_3", "usr_me", _profiles.value.first { it.id == "usr_chika" }, "3h ago")
            )
        )
    )
    val profileVisits: StateFlow<Map<String, List<ProfileVisit>>> = _profileVisits.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "notif_1",
                type = "mention",
                title = "Mentioned in Manila Central",
                message = "Maria Clara mentioned you: '@maria_clara! Buti na lang naka-tambay sa Phchat.'",
                timestamp = "10:15 AM",
                targetRoomId = "room_ncr"
            ),
            NotificationItem(
                id = "notif_2",
                type = "wall",
                title = "New Profile Wall Comment",
                message = "Maria Clara left a comment on your wall: 'Thanks for the warm welcome...'",
                timestamp = "Yesterday"
            ),
            NotificationItem(
                id = "notif_3",
                type = "checkin",
                title = "Daily Check-in Streak 🔥",
                message = "You received 50 Tambay points for maintaining a 5-day streak!",
                timestamp = "Today 8:00 AM",
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Daily Check-in status
    private val _hasCheckedInToday = MutableStateFlow(false)
    val hasCheckedInToday: StateFlow<Boolean> = _hasCheckedInToday.asStateFlow()

    // ---------------- Actions ----------------

    fun checkInToday(): Boolean {
        if (_hasCheckedInToday.value) return false
        val current = _currentUser.value
        val newStreak = current.streak + 1
        val earnedPoints = 50 + (newStreak * 10)
        _currentUser.value = current.copy(
            streak = newStreak,
            points = current.points + earnedPoints
        )
        _hasCheckedInToday.value = true

        val newNotif = NotificationItem(
            id = UUID.randomUUID().toString(),
            type = "checkin",
            title = "Daily Check-in Successful! 🎉",
            message = "Galing! Added +$earnedPoints points. Day $newStreak streak!",
            timestamp = currentTimeString(),
            isRead = false
        )
        _notifications.value = listOf(newNotif) + _notifications.value
        return true
    }

    fun updateMyStatus(newStatus: String) {
        _currentUser.value = _currentUser.value.copy(statusText = newStatus)
    }

    fun updateMyBio(newBio: String, newProvince: String) {
        _currentUser.value = _currentUser.value.copy(
            bio = newBio,
            province = newProvince
        )
    }

    fun sendRoomMessage(
        roomId: String,
        body: String,
        replyTo: ReplySummary? = null
    ) {
        if (body.isBlank()) return
        val me = _currentUser.value

        // Check for mentions
        val mentionMatches = Regex("@(\\w+)").findAll(body).map { it.groupValues[1] }.toList()

        val msg = RoomMessage(
            id = UUID.randomUUID().toString(),
            roomId = roomId,
            senderId = me.id,
            senderName = me.displayName,
            senderAvatarHex = me.avatarColorHex,
            senderRole = MemberRole.ADMIN,
            body = body.trim(),
            kind = MessageKind.TEXT,
            replyTo = replyTo,
            mentions = mentionMatches,
            timestamp = currentTimeString()
        )

        val currentList = _roomMessages.value[roomId] ?: emptyList()
        _roomMessages.value = _roomMessages.value + (roomId to (currentList + msg))

        // Trigger notification if mention
        if (mentionMatches.isNotEmpty()) {
            val notif = NotificationItem(
                id = UUID.randomUUID().toString(),
                type = "mention",
                title = "Mentioned in room",
                message = "${me.displayName} tagged @${mentionMatches.joinToString(", @")}",
                timestamp = currentTimeString(),
                targetRoomId = roomId
            )
            _notifications.value = listOf(notif) + _notifications.value
        }
    }

    fun sendRoomSticker(roomId: String, sticker: ChatSticker) {
        val me = _currentUser.value
        val msg = RoomMessage(
            id = UUID.randomUUID().toString(),
            roomId = roomId,
            senderId = me.id,
            senderName = me.displayName,
            senderAvatarHex = me.avatarColorHex,
            senderRole = MemberRole.ADMIN,
            body = sticker.tagalogPhrase,
            kind = MessageKind.STICKER,
            stickerTitle = sticker.title,
            stickerEmoji = sticker.emoji,
            timestamp = currentTimeString()
        )
        val currentList = _roomMessages.value[roomId] ?: emptyList()
        _roomMessages.value = _roomMessages.value + (roomId to (currentList + msg))
    }

    fun toggleMessageReaction(roomId: String, messageId: String, emoji: String) {
        val currentList = _roomMessages.value[roomId] ?: return
        val updated = currentList.map { msg ->
            if (msg.id == messageId) {
                val currentReactions = msg.reactions.toMutableMap()
                val isMyReaction = msg.myReaction == emoji
                if (isMyReaction) {
                    val count = (currentReactions[emoji] ?: 1) - 1
                    if (count <= 0) currentReactions.remove(emoji) else currentReactions[emoji] = count
                    msg.copy(reactions = currentReactions, myReaction = null)
                } else {
                    currentReactions[emoji] = (currentReactions[emoji] ?: 0) + 1
                    msg.copy(reactions = currentReactions, myReaction = emoji)
                }
            } else msg
        }
        _roomMessages.value = _roomMessages.value + (roomId to updated)
    }

    fun deleteMessage(roomId: String, messageId: String) {
        val currentList = _roomMessages.value[roomId] ?: return
        val updated = currentList.map { msg ->
            if (msg.id == messageId) {
                msg.copy(
                    body = "[Message deleted by moderator]",
                    isDeleted = true
                )
            } else msg
        }
        _roomMessages.value = _roomMessages.value + (roomId to updated)
    }

    fun pinRoomMessage(roomId: String, message: RoomMessage) {
        _rooms.value = _rooms.value.map { r ->
            if (r.id == roomId) r.copy(pinnedMessage = message) else r
        }
    }

    fun toggleRoomPinned(roomId: String) {
        _rooms.value = _rooms.value.map { r ->
            if (r.id == roomId) r.copy(isPinned = !r.isPinned) else r
        }
    }

    fun toggleJoinRoom(roomId: String) {
        _rooms.value = _rooms.value.map { r ->
            if (r.id == roomId) {
                val newJoined = !r.isJoined
                r.copy(
                    isJoined = newJoined,
                    memberCount = if (newJoined) r.memberCount + 1 else r.memberCount - 1
                )
            } else r
        }
    }

    fun updateRoomAnnouncement(roomId: String, newAnnouncement: String) {
        _rooms.value = _rooms.value.map { r ->
            if (r.id == roomId) r.copy(announcement = newAnnouncement) else r
        }
    }

    fun toggleRoomLock(roomId: String) {
        _rooms.value = _rooms.value.map { r ->
            if (r.id == roomId) r.copy(isLocked = !r.isLocked) else r
        }
    }

    fun createRoom(
        name: String,
        provinceCode: String,
        provinceName: String,
        announcement: String
    ): Room {
        val newRoom = Room(
            id = "room_" + UUID.randomUUID().toString().take(6),
            name = name,
            slug = name.lowercase().replace(" ", "-").replace(Regex("[^a-z0-9-]"), ""),
            kind = RoomKind.PUBLIC,
            provinceCode = provinceCode,
            provinceName = provinceName,
            announcement = announcement,
            memberCount = 1,
            onlineCount = 1,
            isJoined = true,
            isPinned = false,
            myRole = MemberRole.OWNER,
            colorHex = 0xFF0038A8
        )
        _rooms.value = listOf(newRoom) + _rooms.value
        return newRoom
    }

    // Direct Messages
    fun sendDirectMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val me = _currentUser.value
        val dm = DirectMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = me.id,
            body = text.trim(),
            timestamp = currentTimeString()
        )
        val current = _directMessages.value[conversationId] ?: emptyList()
        _directMessages.value = _directMessages.value + (conversationId to (current + dm))

        // Update conversation preview
        _conversations.value = _conversations.value.map { c ->
            if (c.id == conversationId) {
                c.copy(
                    lastMessage = text.trim(),
                    lastMessageTime = currentTimeString()
                )
            } else c
        }
    }

    fun startOrGetConversation(participant: Profile): Conversation {
        val existing = _conversations.value.firstOrNull { it.participant.id == participant.id }
        if (existing != null) return existing
        val newConv = Conversation(
            id = "conv_" + participant.username,
            participant = participant,
            lastMessage = "Started a conversation",
            lastMessageTime = "Just now",
            unreadCount = 0
        )
        _conversations.value = listOf(newConv) + _conversations.value
        return newConv
    }

    // Community Content
    fun votePoll(postId: String, optionId: String) {
        _contentPosts.value = _contentPosts.value.map { post ->
            if (post.id == postId && post.poll != null && !post.poll.hasVoted) {
                val updatedOptions = post.poll.options.map { opt ->
                    if (opt.id == optionId) opt.copy(votes = opt.votes + 1) else opt
                }
                post.copy(
                    poll = post.poll.copy(
                        options = updatedOptions,
                        totalVotes = post.poll.totalVotes + 1,
                        hasVoted = true,
                        selectedOptionId = optionId
                    )
                )
            } else post
        }
    }

    fun togglePostLike(postId: String) {
        _contentPosts.value = _contentPosts.value.map { post ->
            if (post.id == postId) {
                val wasLiked = post.isLiked
                post.copy(
                    isLiked = !wasLiked,
                    likesCount = if (wasLiked) post.likesCount - 1 else post.likesCount + 1
                )
            } else post
        }
    }

    fun togglePostSave(postId: String) {
        _contentPosts.value = _contentPosts.value.map { post ->
            if (post.id == postId) post.copy(isSaved = !post.isSaved) else post
        }
    }

    fun createPost(
        title: String,
        body: String,
        category: String,
        pollOptions: List<String> = emptyList()
    ) {
        val me = _currentUser.value
        val hasPoll = pollOptions.isNotEmpty()
        val pollData = if (hasPoll) {
            PollData(
                id = UUID.randomUUID().toString(),
                question = title,
                options = pollOptions.mapIndexed { index, text ->
                    PollOption("opt_$index", text, 0)
                },
                totalVotes = 0
            )
        } else null

        val newPost = ContentPost(
            id = UUID.randomUUID().toString(),
            author = me,
            title = title,
            body = body,
            category = category,
            kind = if (hasPoll) ContentKind.POLL else ContentKind.POST,
            poll = pollData,
            likesCount = 0,
            commentsCount = 0,
            createdAt = "Just now"
        )
        _contentPosts.value = listOf(newPost) + _contentPosts.value
    }

    // Profile Comments / Wall
    fun addProfileComment(profileId: String, commentBody: String) {
        if (commentBody.isBlank()) return
        val me = _currentUser.value
        val comment = ProfileComment(
            id = UUID.randomUUID().toString(),
            profileId = profileId,
            author = me,
            body = commentBody.trim(),
            votes = 1,
            userVote = 1,
            createdAt = "Just now"
        )
        val current = _profileComments.value[profileId] ?: emptyList()
        _profileComments.value = _profileComments.value + (profileId to (listOf(comment) + current))
    }

    fun voteProfileComment(profileId: String, commentId: String, delta: Int) {
        val current = _profileComments.value[profileId] ?: return
        val updated = current.map { c ->
            if (c.id == commentId) {
                if (c.userVote == delta) {
                    // cancel vote
                    c.copy(votes = c.votes - delta, userVote = 0)
                } else {
                    val scoreChange = if (c.userVote == 0) delta else delta * 2
                    c.copy(votes = c.votes + scoreChange, userVote = delta)
                }
            } else c
        }
        _profileComments.value = _profileComments.value + (profileId to updated)
    }

    fun recordProfileVisit(profileId: String) {
        val me = _currentUser.value
        if (profileId == me.id) return
        val currentVisits = _profileVisits.value[profileId] ?: emptyList()
        val visit = ProfileVisit(
            id = UUID.randomUUID().toString(),
            profileId = profileId,
            visitor = me,
            visitedAt = "Just now"
        )
        _profileVisits.value = _profileVisits.value + (profileId to (listOf(visit) + currentVisits.take(19)))
    }

    fun toggleFollowUser(userId: String) {
        _profiles.value = _profiles.value.map { p ->
            if (p.id == userId) p.copy(isFollowed = !p.isFollowed) else p
        }
    }

    fun toggleBlockUser(userId: String) {
        _profiles.value = _profiles.value.map { p ->
            if (p.id == userId) p.copy(isBlocked = !p.isBlocked) else p
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
    }
}
