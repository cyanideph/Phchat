package com.example.phchat.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object PhchatShapes {
    val small = RoundedCornerShape(8.dp)
    val compact = RoundedCornerShape(12.dp)
    val medium = RoundedCornerShape(16.dp)
    val large = RoundedCornerShape(20.dp)
    val pill = RoundedCornerShape(percent = 50)
    val bottomSheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

    fun messageBubble(isMe: Boolean) = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isMe) 16.dp else 4.dp,
        bottomEnd = if (isMe) 4.dp else 16.dp
    )

    fun material() = Shapes(
        extraSmall = small,
        small = compact,
        medium = medium,
        large = large,
        extraLarge = large
    )
}