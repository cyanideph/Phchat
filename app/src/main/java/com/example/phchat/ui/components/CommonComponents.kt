package com.example.phchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.phchat.model.ChatSticker
import com.example.phchat.model.MemberRole
import com.example.phchat.model.StickerPacks
import com.example.phchat.ui.theme.*

@Composable
fun PhchatBackdrop(modifier: Modifier = Modifier, intensity: Float = 1f) {
    val background = MaterialTheme.colorScheme.background
    val violet = PhchatVioletStrong.copy(alpha = 0.16f * intensity)
    val lime = PhchatLime.copy(alpha = 0.11f * intensity)
    val mint = PhchatMint.copy(alpha = 0.07f * intensity)
    val grid = PhchatLavender300.copy(alpha = 0.07f * intensity)

    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRect(color = background)
        drawCircle(violet, radius = w * 0.52f, center = androidx.compose.ui.geometry.Offset(w * 0.88f, h * 0.06f))
        drawCircle(lime, radius = w * 0.30f, center = androidx.compose.ui.geometry.Offset(w * 0.12f, h * 0.28f))
        drawCircle(mint, radius = w * 0.34f, center = androidx.compose.ui.geometry.Offset(w * 0.82f, h * 0.78f))

        val step = 36.dp.toPx()
        var x = -h
        while (x < w + h) {
            drawLine(
                color = grid,
                start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x + h, h),
                strokeWidth = 1.dp.toPx()
            )
            x += step
        }
    }
}

@Composable
fun PhchatMark(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    backgroundColor: Color = PhchatLime,
    foregroundColor: Color = PhchatNavy950,
    contentDescription: String = "Phchat"
) {
    Box(
        modifier = modifier.size(size).clip(RoundedCornerShape(size * 0.28f)).background(backgroundColor)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.19f)) {
            val iconSize = this.size.minDimension
            val stroke = iconSize * 0.095f
            drawRoundRect(
                color = foregroundColor,
                topLeft = androidx.compose.ui.geometry.Offset(iconSize * 0.06f, iconSize * 0.06f),
                size = androidx.compose.ui.geometry.Size(iconSize * 0.88f, iconSize * 0.72f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(iconSize * 0.18f, iconSize * 0.18f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
            )
            val cy = iconSize * 0.42f
            val leftNode = iconSize * 0.30f
            val midNode = iconSize * 0.50f
            val rightNode = iconSize * 0.70f
            drawLine(foregroundColor, androidx.compose.ui.geometry.Offset(leftNode, cy), androidx.compose.ui.geometry.Offset(rightNode, cy),
                strokeWidth = stroke * 0.72f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawCircle(foregroundColor, stroke * 0.72f, androidx.compose.ui.geometry.Offset(leftNode, cy))
            drawCircle(foregroundColor, stroke * 0.72f, androidx.compose.ui.geometry.Offset(midNode, cy))
            drawCircle(foregroundColor, stroke * 0.72f, androidx.compose.ui.geometry.Offset(rightNode, cy))
            drawLine(foregroundColor, androidx.compose.ui.geometry.Offset(iconSize * 0.30f, iconSize * 0.77f),
                androidx.compose.ui.geometry.Offset(iconSize * 0.24f, iconSize * 0.93f), strokeWidth = stroke,
                cap = androidx.compose.ui.graphics.StrokeCap.Round)
        }
    }
}

@Composable
fun UserAvatar(initial: String, colorHex: Long, modifier: Modifier = Modifier, size: Dp = 40.dp, isActive: Boolean? = null, onClick: (() -> Unit)? = null) {
    Box(modifier = modifier.size(size).clip(CircleShape).background(Color(colorHex))
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier), contentAlignment = Alignment.Center) {
        Text(initial.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size.value * 0.42f).sp)
        if (isActive != null) {
            Box(Modifier.align(Alignment.BottomEnd).size(size * 0.32f).clip(CircleShape).background(Color.White)
                .padding(1.5.dp).clip(CircleShape).background(if (isActive) StatusOnline else StatusAway))
        }
    }
}

@Composable
fun RoleBadge(role: MemberRole, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when (role) {
        MemberRole.OWNER -> Triple("OWNER", RoleOwner.copy(alpha = 0.15f), RoleOwner)
        MemberRole.ADMIN -> Triple("ADMIN", RoleAdmin.copy(alpha = 0.15f), RoleAdmin)
        MemberRole.MODERATOR -> Triple("MOD", RoleMod.copy(alpha = 0.15f), RoleMod)
        MemberRole.MEMBER -> return
    }
    Surface(color = bg, shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
        Text(label, color = fg, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun ProvinceBadge(code: String, name: String, modifier: Modifier = Modifier) {
    val (glyph, bg, fg) = when (code.uppercase()) {
        "NCR", "MNL" -> Triple("NCR", PhchatMint.copy(alpha = 0.12f), PhchatMint)
        "CEB", "ILO", "NEG" -> Triple(code.uppercase(), PhchatViolet.copy(alpha = 0.14f), PhchatVioletStrong)
        "DVO" -> Triple("DVO", PhchatMint.copy(alpha = 0.12f), PhchatMint)
        "PAM", "BUL", "BAG", "BEN" -> Triple(code.uppercase(), PhchatLime.copy(alpha = 0.14f), PhchatLime)
        "ALB", "CAM" -> Triple("ALB", PhchatDanger.copy(alpha = 0.14f), PhchatDanger)
        else -> Triple(code.ifBlank { "•" }, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
    Surface(color = bg, shape = MaterialTheme.shapes.extraSmall, modifier = modifier) {
        Text(glyph, color = fg, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp))
    }
}

@Composable
fun PhchatLiveStrip(roomCount: Int, onlineCount: Int = 18, modifier: Modifier = Modifier) {
    Surface(color = PhchatNavy950, shape = MaterialTheme.shapes.small, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(PhchatLime))
                Spacer(Modifier.width(6.dp))
                Text("LIVE NOW", color = PhchatLime, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.width(6.dp))
                Text("$roomCount rooms", color = PhchatTextOnDark.copy(alpha = 0.9f), fontSize = 10.sp)
            }
            Text("$onlineCount online", color = PhchatMint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StreakPointsCard(streak: Int, points: Int, hasCheckedIn: Boolean, onCheckInClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().testTag("streak_card"), shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = PhchatVioletDeep), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(50.dp).clip(CircleShape).background(PhchatLime), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = PhchatNavy950, modifier = Modifier.size(30.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("$streak-day activity streak", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = "Points", tint = PhchatMint, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("$points points", fontSize = 13.sp, color = PhchatLavender100, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Button(onClick = onCheckInClick, enabled = !hasCheckedIn,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant, disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                shape = MaterialTheme.shapes.small, modifier = Modifier.testTag("checkin_button")) {
                Text(if (hasCheckedIn) "Checked in" else "Check in +50", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun PhchatStickerDrawer(onStickerSelected: (ChatSticker) -> Unit, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().height(280.dp).testTag("sticker_drawer"),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Chat stickers", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close Stickers") }
            }
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                items(StickerPacks.pinoyStickers) { sticker ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onStickerSelected(sticker) }.testTag("sticker_" + sticker.id),
                        shape = MaterialTheme.shapes.small, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(sticker.emoji, fontSize = 28.sp)
                            Spacer(Modifier.height(2.dp))
                            Text(sticker.title, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerPickerSheet(onDismiss: () -> Unit, onSelectSticker: (ChatSticker) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        PhchatStickerDrawer(onStickerSelected = { onSelectSticker(it); onDismiss() }, onDismiss = onDismiss)
    }
}

@Deprecated("Use PhchatLiveStrip")
@Composable
fun UzzapRetroTicker(roomCount: Int, onlineCount: Int = 18, modifier: Modifier = Modifier) = PhchatLiveStrip(roomCount, onlineCount, modifier)

@Deprecated("Use PhchatStickerDrawer")
@Composable
fun PinoyStickerDrawer(onStickerSelected: (ChatSticker) -> Unit, onDismiss: () -> Unit) = PhchatStickerDrawer(onStickerSelected, onDismiss)

@Composable
fun QuickReactionRow(onSelectEmoji: (String) -> Unit, modifier: Modifier = Modifier) {
    val emojis = listOf("👍", "❤️", "🔥", "😂", "🙏")
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        emojis.forEach { emoji ->
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp).clickable { onSelectEmoji(emoji) }) {
                Box(contentAlignment = Alignment.Center) { Text(emoji, fontSize = 20.sp) }
            }
        }
    }
}
