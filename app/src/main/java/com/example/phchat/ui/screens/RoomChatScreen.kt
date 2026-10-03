package com.example.phchat.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phchat.model.*
import com.example.phchat.ui.components.*
import com.example.phchat.ui.dialogs.*
import com.example.phchat.ui.theme.*
import com.example.phchat.viewmodel.PhchatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomChatScreen(
    roomId: String,
    viewModel: PhchatViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val rooms by viewModel.rooms.collectAsState()
    val room = rooms.firstOrNull { it.id == roomId } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Room not found")
        }
        return
    }

    val messagesMap by viewModel.roomMessages.collectAsState()
    val messages = messagesMap[roomId] ?: emptyList()
    val currentUser by viewModel.currentUser.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<ReplySummary?>(null) }
    var showStickerSheet by remember { mutableStateOf(false) }
    var showMembersSheet by remember { mutableStateOf(false) }
    var reportingUser by remember { mutableStateOf<Profile?>(null) }
    var blockingUser by remember { mutableStateOf<Profile?>(null) }
    var selectedMessageForMenu by remember { mutableStateOf<RoomMessage?>(null) }
    var showRoomSettingsMenu by remember { mutableStateOf(false) }
    var showAnnouncementDialog by remember { mutableStateOf(false) }

    val profiles by viewModel.profiles.collectAsState()

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.sendRoomMessage(room.id, "📷 Nag-padala ng larawan: $uri")
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (room.isLocked) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = PhRedSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${room.provinceName} • ${room.onlineCount} online",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.sendKalabit(room.id) },
                        modifier = Modifier.testTag("kalabit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Kalabit / Buzz",
                            tint = PhAcidLime
                        )
                    }
                    IconButton(
                        onClick = { showMembersSheet = true },
                        modifier = Modifier.testTag("members_directory_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Mga Tambay sa Loob"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleRoomPinned(room.id) }) {
                        Icon(
                            imageVector = if (room.isPinned) Icons.Default.PushPin else Icons.Default.BookmarkBorder,
                            contentDescription = "Pin",
                            tint = if (room.isPinned) PhAcidLime else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box {
                        IconButton(onClick = { showRoomSettingsMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showRoomSettingsMenu,
                            onDismissRequest = { showRoomSettingsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (room.isLocked) "Unlock Room" else "Lock Room") },
                                onClick = {
                                    viewModel.toggleRoomLock(room.id)
                                    showRoomSettingsMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (room.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Announcement") },
                                onClick = {
                                    showRoomSettingsMenu = false
                                    showAnnouncementDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Campaign, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (room.isJoined) "Leave Room" else "Join Room") },
                                onClick = {
                                    viewModel.toggleJoinRoom(room.id)
                                    showRoomSettingsMenu = false
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Reply banner
                if (replyingTo != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Replying to ${replyingTo?.senderName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PhAcidLime
                                )
                                Text(
                                    text = replyingTo?.snippet ?: "",
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = { replyingTo = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel reply", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Quick Pinoy Expression Pills
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickPills = listOf(
                        "☕ Kape Muna",
                        "🚀 Tara G!",
                        "🙏 Salamat Lodi",
                        "✨ Sana All",
                        "👏 Edi Wow",
                        "👋 Kumusta",
                        "💖 Lablab"
                    )
                    items(quickPills) { phrase ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PhAcidLime.copy(alpha = 0.16f),
                            modifier = Modifier.clickable {
                                viewModel.sendRoomMessage(room.id, phrase, replyingTo)
                                replyingTo = null
                            }
                        ) {
                            Text(
                                text = phrase,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PhBlack,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Chat Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showStickerSheet = true },
                        modifier = Modifier.testTag("sticker_button")
                    ) {
                        Icon(Icons.Default.EmojiEmotions, contentDescription = "Stickers")
                    }

                    IconButton(
                        onClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.testTag("attach_photo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Photo",
                            tint = PhAcidLime
                        )
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Tambay chat / use @username...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("message_input"),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendRoomMessage(room.id, inputText, replyingTo)
                                inputText = ""
                                replyingTo = null
                                coroutineScope.launch {
                                    if (messages.isNotEmpty()) {
                                        listState.animateScrollToItem(messages.size - 1)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank()) PhAcidLime else MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) PhBlack else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Room announcement / Pinned banner
            if (room.announcement.isNotBlank() || room.pinnedMessage != null) {
                Surface(
                    color = PhAcidLime.copy(alpha = 0.16f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = PhAcidLime,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = room.announcement.ifBlank { "Pinned: " + room.pinnedMessage?.body },
                            fontSize = 12.sp,
                            color = PhBlack,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Message list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { message ->
                    if (message.kind == MessageKind.SYSTEM) {
                        SystemMessageBubble(message = message)
                    } else {
                        RoomMessageBubble(
                            message = message,
                            isMe = message.senderId == currentUser.id,
                            onClick = { selectedMessageForMenu = message },
                            onReactionClick = { emoji ->
                                viewModel.toggleMessageReaction(room.id, message.id, emoji)
                            },
                            onSenderClick = { viewModel.openProfile(message.senderId) }
                        )
                    }
                }
            }
        }
    }

    // Sticker Picker Sheet
    if (showStickerSheet) {
        StickerPickerSheet(
            onDismiss = { showStickerSheet = false },
            onSelectSticker = { sticker ->
                viewModel.sendRoomSticker(room.id, sticker)
            }
        )
    }

    // Selected Message Actions Modal
    if (selectedMessageForMenu != null) {
        val msg = selectedMessageForMenu!!
        AlertDialog(
            onDismissRequest = { selectedMessageForMenu = null },
            title = {
                Text("Message from ${msg.senderName}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Quick Reactions:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    QuickReactionRow(
                        onSelectEmoji = { emoji ->
                            viewModel.toggleMessageReaction(room.id, msg.id, emoji)
                            selectedMessageForMenu = null
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            replyingTo = ReplySummary(msg.id, msg.senderName, msg.body)
                            selectedMessageForMenu = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reply")
                    }

                    Button(
                        onClick = {
                            viewModel.pinRoomMessage(room.id, msg)
                            selectedMessageForMenu = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pin to Room")
                    }

                    if (msg.senderId == currentUser.id || room.myRole == MemberRole.ADMIN || room.myRole == MemberRole.OWNER) {
                        Button(
                            onClick = {
                                viewModel.deleteMessage(room.id, msg.id)
                                selectedMessageForMenu = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PhRedContainer, contentColor = PhOnRedContainer)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Message")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedMessageForMenu = null }) {
                    Text("Close")
                }
            }
        )
    }

    if (showAnnouncementDialog) {
        var announcementInput by remember { mutableStateOf(room.announcement) }
        AlertDialog(
            onDismissRequest = { showAnnouncementDialog = false },
            title = { Text("Update Room Announcement") },
            text = {
                OutlinedTextField(
                    value = announcementInput,
                    onValueChange = { announcementInput = it },
                    label = { Text("Announcement text") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateRoomAnnouncement(room.id, announcementInput)
                    showAnnouncementDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAnnouncementDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showMembersSheet) {
        RoomMemberDirectorySheet(
            roomName = room.name,
            members = profiles,
            currentUserId = currentUser.id,
            currentUserRole = room.myRole,
            onDismiss = { showMembersSheet = false },
            onWhisper = { member ->
                inputText = "@${member.username} "
            },
            onViewProfile = { member ->
                viewModel.openProfile(member.id)
            },
            onAddBuddy = { member ->
                viewModel.addBuddy(member.id) { _, _ -> }
            },
            onReportUser = { member ->
                reportingUser = member
            },
            onBlockUser = { member ->
                blockingUser = member
            },
            onStrikeUser = { member ->
                viewModel.sendRoomMessage(
                    room.id,
                    "⚠️ [MOD STRIKE] Nakatanggap si @${member.username} ng babala mula sa pamunuan!",
                    kind = MessageKind.SYSTEM
                )
            }
        )
    }

    val repUser = reportingUser
    if (repUser != null) {
        ReportDialog(
            targetName = repUser.displayName,
            onDismiss = { reportingUser = null },
            onSubmitReport = { reason, details ->
                viewModel.submitReport(repUser.id, reason, details) {
                    reportingUser = null
                }
            }
        )
    }

    val blkUser = blockingUser
    if (blkUser != null) {
        BlockUserDialog(
            userName = blkUser.displayName,
            onDismiss = { blockingUser = null },
            onConfirmBlock = {
                viewModel.blockUser(blkUser.id)
                blockingUser = null
            }
        )
    }
}

@Composable
fun SystemMessageBubble(message: RoomMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = message.body,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun RoomMessageBubble(
    message: RoomMessage,
    isMe: Boolean,
    onClick: () -> Unit,
    onReactionClick: (String) -> Unit,
    onSenderClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("message_${message.id}"),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            UserAvatar(
                initial = message.senderName,
                colorHex = message.senderAvatarHex,
                size = 36.dp,
                onClick = onSenderClick
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            if (!isMe) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    RoleBadge(role = message.senderRole)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = message.timestamp,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Reply Quote
            if (message.replyTo != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text(
                            text = "Replying to ${message.replyTo.senderName}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PhAcidLime
                        )
                        Text(
                            text = message.replyTo.snippet,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Message Body (Sticker or Text)
            Surface(
                color = when {
                    message.isDeleted -> MaterialTheme.colorScheme.surfaceVariant
                    isMe -> PhAcidLime
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                shadowElevation = 1.dp,
                modifier = Modifier.clickable { onClick() }
            ) {
                if (message.kind == MessageKind.STICKER && !message.isDeleted) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = message.stickerEmoji ?: "🇵🇭", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message.body,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isMe) PhBlack else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Pinoy Sticker",
                            fontSize = 9.sp,
                            color = if (isMe) PhBlack.copy(alpha = 0.62f) else MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text(
                            text = message.body,
                            color = when {
                                message.isDeleted -> MaterialTheme.colorScheme.outline
                                isMe -> PhBlack
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontSize = 14.sp
                        )
                        if (isMe) {
                            Text(
                                text = message.timestamp,
                                color = PhBlack.copy(alpha = 0.62f),
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            // Reaction badges
            if (message.reactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    message.reactions.forEach { (emoji, count) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 1.dp,
                            modifier = Modifier.clickable { onReactionClick(emoji) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "$count",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
