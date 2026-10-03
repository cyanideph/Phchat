package com.example.phchat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.phchat.model.ChatSticker
import com.example.phchat.model.MemberRole
import com.example.phchat.model.StickerPacks
import com.example.phchat.ui.theme.*

@Composable
fun PhchatBackdrop(
    modifier: Modifier = Modifier,
    intensity: Float = 1f
) {
    val background = MaterialTheme.colorScheme.background
    val lime = PhBlueLight.copy(alpha = 0.10f * intensity)
    val amber = PhYellowSun.copy(alpha = 0.08f * intensity)
    val graphite = Color(0xFF1A1A17).copy(alpha = 0.16f * intensity)

    androidx.compose.foundation.Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        drawRect(color = background)

        drawCircle(
            color = lime,
            radius = w * 0.48f,
            center = androidx.compose.ui.geometry.Offset(w * 0.86f, h * 0.08f)
        )
        drawCircle(
            color = amber,
            radius = w * 0.32f,
            center = androidx.compose.ui.geometry.Offset(w * 0.08f, h * 0.78f)
        )

        val step = 34.dp.toPx()
        var x = -h
        while (x < w + h) {
            drawLine(
                color = graphite,
                start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x + h, h),
                strokeWidth = 1.dp.toPx()
            )
            x += step
        }
    }
}

@Composable
fun UserAvatar(
    initial: String,
    colorHex: Long,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isActive: Boolean? = null,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(colorHex))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.take(1).uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.42f).sp
        )

        if (isActive != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.32f)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(1.5.dp)
                    .clip(CircleShape)
                    .background(if (isActive) StatusOnline else StatusAway)
            )
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

    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ProvinceBadge(code: String, name: String, modifier: Modifier = Modifier) {
    val (glyph, bg, fg) = when (code.uppercase()) {
        "NCR", "MNL" -> Triple("NCR", RegionNcr.copy(alpha = 0.12f), RegionNcr)
        "CEB" -> Triple("CEB", RegionVisayas.copy(alpha = 0.15f), RegionVisayas)
        "DVO" -> Triple("DVO", RegionMindanao.copy(alpha = 0.15f), RegionMindanao)
        "PAM", "BUL" -> Triple("PAM", RegionLuzon.copy(alpha = 0.15f), RegionLuzon)
        "ILO", "NEG" -> Triple("ILO", RegionVisayas.copy(alpha = 0.15f), RegionVisayas)
        "BAG", "BEN" -> Triple("BAG", RegionMindanao.copy(alpha = 0.15f), RegionMindanao)
        "ALB", "CAM" -> Triple("ALB", RegionBicol.copy(alpha = 0.15f), RegionBicol)
        else -> Triple("•", PhBlueContainer, PhOnBlueContainer)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = if (glyph == "•") code else glyph,
                color = fg,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun UzzapRetroTicker(
    roomCount: Int,
    onlineCount: Int = 18,
    modifier: Modifier = Modifier
) {
    Surface(
        color = PhBlueDark,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StatusOnline)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "LIVE NOW",
                    color = PhYellowSun,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$roomCount rooms",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "$onlineCount online",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StreakPointsCard(
    streak: Int,
    points: Int,
    hasCheckedIn: Boolean,
    onCheckInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = PhBluePrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(PhYellowSun),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = PhRedSecondary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "$streak-day activity streak",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Points",
                            tint = PhYellowSun,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$points points",
                            fontSize = 13.sp,
                            color = PhGoldContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Button(
                onClick = onCheckInClick,
                enabled = !hasCheckedIn,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PhYellowSun,
                    contentColor = Color(0xFF4A3800),
                    disabledContainerColor = Color.White.copy(alpha = 0.25f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("checkin_button")
            ) {
                Text(
                    text = if (hasCheckedIn) "Checked in" else "Check in +50",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PinoyStickerDrawer(
    onStickerSelected: (ChatSticker) -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("sticker_drawer"),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Chat stickers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PhBluePrimary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close Stickers")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(StickerPacks.pinoyStickers) { sticker ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStickerSelected(sticker) }
                            .testTag("sticker_${sticker.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = sticker.emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = sticker.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerPickerSheet(
    onDismiss: () -> Unit,
    onSelectSticker: (ChatSticker) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        PinoyStickerDrawer(
            onStickerSelected = {
                onSelectSticker(it)
                onDismiss()
            },
            onDismiss = onDismiss
        )
    }
}

@Composable
fun QuickReactionRow(
    onSelectEmoji: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val emojis = listOf("👍", "❤️", "🔥", "😂", "🙏")
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        emojis.forEach { emoji ->
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onSelectEmoji(emoji) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }
        }
    }
}