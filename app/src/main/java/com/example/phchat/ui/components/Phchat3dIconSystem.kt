package com.example.phchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Prominent visual language for Phchat.
 *
 * 3Dicons are used for feature/empty-state visuals, while PhchatIconView remains
 * the local vector system for small controls and navigation.
 *
 * The image URLs point to the CC0 V1 assets published by 3dicons.
 * Core interaction controls never depend on these network assets.
 */
enum class Phchat3dIcon {
    CHAT,
    CAMERA,
    MOBILE,
    PHONE_RINGING,
    VIDEO_CAMERA,
    MIC,
    SEARCH,
    SETTING,
    LOCK,
    PICTURE,
    BOY,
    GIRL
}

private const val CDN = "https://3dicons.sgp1.cdn.digitaloceanspaces.com/v1/dynamic/color"

private val Phchat3dIcon.assetUrl: String
    get() = when (this) {
        Phchat3dIcon.CHAT -> "$CDN/chat-dynamic-color.png"
        Phchat3dIcon.CAMERA -> "$CDN/camera-dynamic-color.png"
        Phchat3dIcon.MOBILE -> "$CDN/mobile-dynamic-color.png"
        Phchat3dIcon.PHONE_RINGING -> "$CDN/phone-ringing-dynamic-color.png"
        Phchat3dIcon.VIDEO_CAMERA -> "$CDN/video-camera-dynamic-color.png"
        Phchat3dIcon.MIC -> "$CDN/mic-dynamic-color.png"
        Phchat3dIcon.SEARCH -> "$CDN/zoom-dynamic-color.png"
        Phchat3dIcon.SETTING -> "$CDN/setting-dynamic-color.png"
        Phchat3dIcon.LOCK -> "$CDN/lock-dynamic-color.png"
        Phchat3dIcon.PICTURE -> "$CDN/picture-dynamic-color.png"
        Phchat3dIcon.BOY -> "$CDN/boy-dynamic-color.png"
        Phchat3dIcon.GIRL -> "$CDN/girl-dynamic-color.png"
    }

private val Phchat3dIcon.contentDescription: String
    get() = when (this) {
        Phchat3dIcon.CHAT -> "Chat"
        Phchat3dIcon.CAMERA -> "Camera"
        Phchat3dIcon.MOBILE -> "Mobile"
        Phchat3dIcon.PHONE_RINGING -> "Incoming call"
        Phchat3dIcon.VIDEO_CAMERA -> "Video call"
        Phchat3dIcon.MIC -> "Microphone"
        Phchat3dIcon.SEARCH -> "Search"
        Phchat3dIcon.SETTING -> "Settings"
        Phchat3dIcon.LOCK -> "Privacy"
        Phchat3dIcon.PICTURE -> "Picture"
        Phchat3dIcon.BOY -> "Profile"
        Phchat3dIcon.GIRL -> "Profile"
    }

@Composable
fun Phchat3dIconView(
    icon: Phchat3dIcon,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
    contentScale: ContentScale = ContentScale.Fit,
    background: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.18f))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = icon.assetUrl,
            contentDescription = icon.contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
            error = rememberVectorPainter(icon.fallbackVector())
        )
    }
}

private fun Phchat3dIcon.fallbackVector() = when (this) {
    Phchat3dIcon.CHAT -> Icons.Default.Chat
    Phchat3dIcon.CAMERA -> Icons.Default.CameraAlt
    Phchat3dIcon.MOBILE -> Icons.Default.Phone
    Phchat3dIcon.PHONE_RINGING -> Icons.Default.Phone
    Phchat3dIcon.VIDEO_CAMERA -> Icons.Default.Videocam
    Phchat3dIcon.MIC -> Icons.Default.Mic
    Phchat3dIcon.SEARCH -> Icons.Default.Search
    Phchat3dIcon.SETTING -> Icons.Default.Settings
    Phchat3dIcon.LOCK -> Icons.Default.Group
    Phchat3dIcon.PICTURE -> Icons.Default.CameraAlt
    Phchat3dIcon.BOY, Phchat3dIcon.GIRL -> Icons.Default.Group
}
