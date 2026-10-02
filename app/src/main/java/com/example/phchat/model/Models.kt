package com.example.phchat.model

enum class RoomKind {
    PUBLIC,
    PRIVATE,
    GROUP
}

enum class MemberRole {
    OWNER,
    ADMIN,
    MODERATOR,
    MEMBER
}

enum class MessageKind {
    TEXT,
    SYSTEM,
    MEDIA,
    REPLY,
    STICKER
}

enum class ContentKind {
    POST,
    POLL,
    ANNOUNCEMENT
}

data class Profile(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarInitial: String,
    val avatarColorHex: Long,
    val bio: String,
    val statusText: String,
    val province: String,
    val isActive: Boolean = true,
    val lastSeenAt: String = "Just now",
    val points: Int = 0,
    val streak: Int = 0,
    val isFeatured: Boolean = false,
    val isFollowed: Boolean = false,
    val isBlocked: Boolean = false,
    val strikes: Int = 0
)

data class Room(
    val id: String,
    val name: String,
    val slug: String,
    val kind: RoomKind = RoomKind.PUBLIC,
    val provinceCode: String = "ALL", // NCR, CEB, DVO, PAM, ILO, BAG, ALL
    val provinceName: String = "All Philippines",
    val isLocked: Boolean = false,
    val viewOnly: Boolean = false,
    val membersCanInvite: Boolean = true,
    val announcement: String = "",
    val pinnedMessage: RoomMessage? = null,
    val memberCount: Int = 0,
    val onlineCount: Int = 0,
    val isJoined: Boolean = false,
    val isPinned: Boolean = false,
    val myRole: MemberRole = MemberRole.MEMBER,
    val colorHex: Long = 0xFF0038A8
)

data class RoomMember(
    val userId: String,
    val profile: Profile,
    val role: MemberRole = MemberRole.MEMBER,
    val nickname: String? = null,
    val isMuted: Boolean = false,
    val isBanned: Boolean = false,
    val isCoHost: Boolean = false,
    val strikes: Int = 0
)

data class ReplySummary(
    val id: String,
    val senderName: String,
    val snippet: String
)

data class RoomMessage(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatarHex: Long,
    val senderRole: MemberRole = MemberRole.MEMBER,
    val body: String,
    val kind: MessageKind = MessageKind.TEXT,
    val stickerTitle: String? = null,
    val stickerEmoji: String? = null,
    val mediaUrl: String? = null,
    val replyTo: ReplySummary? = null,
    val reactions: Map<String, Int> = emptyMap(),
    val myReaction: String? = null,
    val mentions: List<String> = emptyList(),
    val timestamp: String = "12:00 PM",
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false
)

data class Conversation(
    val id: String,
    val participant: Profile,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false
)

data class DirectMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val body: String,
    val kind: MessageKind = MessageKind.TEXT,
    val stickerEmoji: String? = null,
    val timestamp: String = "12:00 PM"
)

data class PollOption(
    val id: String,
    val text: String,
    val votes: Int = 0
)

data class PollData(
    val id: String,
    val question: String,
    val options: List<PollOption>,
    val totalVotes: Int,
    val hasVoted: Boolean = false,
    val selectedOptionId: String? = null
)

data class ContentPost(
    val id: String,
    val author: Profile,
    val title: String,
    val body: String,
    val category: String, // General, Provincial Buzz, Food & Travel, Gaming, Chismis
    val kind: ContentKind = ContentKind.POST,
    val poll: PollData? = null,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val commentsCount: Int = 0,
    val createdAt: String = "2h ago"
)

data class ContentComment(
    val id: String,
    val postId: String,
    val author: Profile,
    val body: String,
    val votes: Int = 0,
    val userVote: Int = 0,
    val createdAt: String = "1h ago"
)

data class ProfileComment(
    val id: String,
    val profileId: String,
    val author: Profile,
    val body: String,
    val votes: Int = 0,
    val userVote: Int = 0,
    val createdAt: String = "Today"
)

data class ProfileVisit(
    val id: String,
    val profileId: String,
    val visitor: Profile,
    val visitedAt: String = "Just now"
)

data class NotificationItem(
    val id: String,
    val type: String, // mention, reply, reaction, wall, checkin, system
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val targetRoomId: String? = null
)

data class ChatSticker(
    val id: String,
    val title: String,
    val emoji: String,
    val tagalogPhrase: String,
    val category: String
)


data class NotificationPreferences(
    val userId: String = "",
    val followEnabled: Boolean = true,
    val blockEnabled: Boolean = true,
    val contentCommentEnabled: Boolean = true,
    val commentReplyEnabled: Boolean = true,
    val contentReactionEnabled: Boolean = true,
    val roomMessageReactionEnabled: Boolean = true,
    val profileCommentEnabled: Boolean = true,
    val mentionEnabled: Boolean = true,
    val roomInviteEnabled: Boolean = true,
    val conversationInviteEnabled: Boolean = true
)

object StickerPacks {
    val pinoyStickers = listOf(
        ChatSticker("stk_1", "Kumusta", "👋", "Kumusta Ka!", "Greetings"),
        ChatSticker("stk_2", "Salamat", "🙏", "Maraming Salamat Lodi!", "Gratitude"),
        ChatSticker("stk_3", "Ingat", "🛵", "Ingat Lagi sa Byahe!", "Care"),
        ChatSticker("stk_4", "Tara G", "🚀", "Tara G! Sama ako dyan!", "Action"),
        ChatSticker("stk_5", "Kape", "☕", "Kape Muna Tayo!", "Chill"),
        ChatSticker("stk_6", "Chibog", "🍲", "Kainan Na! Chibog time!", "Food"),
        ChatSticker("stk_7", "Sana All", "✨", "Sana All Pinagpala!", "Reaction"),
        ChatSticker("stk_8", "Walang Ganyanan", "😱", "Hala, Walang Ganyanan!", "Shock"),
        ChatSticker("stk_9", "Lodi", "👑", "Petmalu Lodi Werpa!", "Praise"),
        ChatSticker("stk_10", "Lablab", "💖", "Lablab Kita Friend!", "Love"),
        ChatSticker("stk_11", "Edi Wow", "👏", "E di wow, ikaw na!", "Humor"),
        ChatSticker("stk_12", "Tambay", "🪑", "Tambay Mode Muna!", "Chill")
    )
}
