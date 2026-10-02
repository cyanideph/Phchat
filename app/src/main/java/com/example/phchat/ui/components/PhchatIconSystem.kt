package com.example.phchat.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.phchat.ui.theme.PhBlueLight
import com.example.phchat.ui.theme.PhGoldContainer
import com.example.phchat.ui.theme.PhRedSecondary
import com.example.phchat.ui.theme.StatusOnline

enum class PhchatIcon {
    HOME, CHATS, CONTACTS, NOTIFICATIONS, SEARCH, PROFILE,
    NEW_CHAT, CAMERA, MEDIA, VOICE, VIDEO_CALL, PHONE_CALL,
    ATTACHMENT, REACTION, SETTINGS, MORE, FAVORITE, PRIVACY
}

data class PhchatIconSpec(
    val vector: ImageVector,
    val contentDescription: String,
    val accent: Color
)

@Composable
fun phchatIconSpec(icon: PhchatIcon): PhchatIconSpec {
    val scheme = MaterialTheme.colorScheme
    return when (icon) {
        PhchatIcon.HOME -> PhchatIconSpec(Icons.Filled.Home, "Home", scheme.onSurface)
        PhchatIcon.CHATS -> PhchatIconSpec(Icons.Filled.ChatBubble, "Chats", PhBlueLight)
        PhchatIcon.CONTACTS -> PhchatIconSpec(Icons.Filled.Group, "Contacts", scheme.onSurface)
        PhchatIcon.NOTIFICATIONS -> PhchatIconSpec(Icons.Filled.Notifications, "Notifications", PhGoldContainer)
        PhchatIcon.SEARCH -> PhchatIconSpec(Icons.Filled.Search, "Search", scheme.onSurface)
        PhchatIcon.PROFILE -> PhchatIconSpec(Icons.Filled.Person, "Profile", scheme.onSurface)
        PhchatIcon.NEW_CHAT -> PhchatIconSpec(Icons.Filled.AddComment, "New chat", PhBlueLight)
        PhchatIcon.CAMERA -> PhchatIconSpec(Icons.Filled.CameraAlt, "Camera", PhRedSecondary)
        PhchatIcon.MEDIA -> PhchatIconSpec(Icons.Filled.PhotoLibrary, "Media", PhGoldContainer)
        PhchatIcon.VOICE -> PhchatIconSpec(Icons.Filled.Mic, "Voice message", PhBlueLight)
        PhchatIcon.VIDEO_CALL -> PhchatIconSpec(Icons.Filled.Videocam, "Video call", PhBlueLight)
        PhchatIcon.PHONE_CALL -> PhchatIconSpec(Icons.Filled.Call, "Phone call", StatusOnline)
        PhchatIcon.ATTACHMENT -> PhchatIconSpec(Icons.Filled.AttachFile, "Attachment", scheme.onSurface)
        PhchatIcon.REACTION -> PhchatIconSpec(Icons.Filled.EmojiEmotions, "Reaction", PhGoldContainer)
        PhchatIcon.SETTINGS -> PhchatIconSpec(Icons.Filled.Settings, "Settings", scheme.onSurface)
        PhchatIcon.MORE -> PhchatIconSpec(Icons.Filled.MoreHoriz, "More", scheme.onSurface)
        PhchatIcon.FAVORITE -> PhchatIconSpec(Icons.Filled.Favorite, "Favorite", PhRedSecondary)
        PhchatIcon.PRIVACY -> PhchatIconSpec(Icons.Filled.Lock, "Privacy", scheme.onSurface)
    }
}

@Composable
fun PhchatIconView(
    icon: PhchatIcon,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color? = null
) {
    val spec = phchatIconSpec(icon)
    Icon(
        imageVector = spec.vector,
        contentDescription = spec.contentDescription,
        tint = tint ?: spec.accent,
        modifier = modifier.size(size)
    )
}
