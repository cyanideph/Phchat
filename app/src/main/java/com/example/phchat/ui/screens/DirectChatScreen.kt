package com.example.phchat.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phchat.model.MessageKind
import com.example.phchat.ui.components.StickerPickerSheet
import com.example.phchat.ui.components.UserAvatar
import com.example.phchat.ui.theme.PhchatLime
import com.example.phchat.ui.theme.PhchatNavy950
import com.example.phchat.viewmodel.PhchatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectChatScreen(
    conversationId: String,
    viewModel: PhchatViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler {
        onNavigateBack()
    }

    val conversations by viewModel.conversations.collectAsState()
    val conv = conversations.firstOrNull { it.id == conversationId } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Conversation not found")
        }
        return
    }

    val directMessagesMap by viewModel.directMessages.collectAsState()
    val messages = directMessagesMap[conversationId] ?: emptyList()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(conversationId) {
        viewModel.markConversationRead(conversationId)
    }

    var inputText by remember { mutableStateOf("") }
    var showStickerSheet by remember { mutableStateOf(false) }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.openProfile(conv.participant.id) }
                    ) {
                        UserAvatar(
                            initial = conv.participant.avatarInitial,
                            colorHex = conv.participant.avatarColorHex,
                            size = 38.dp,
                            isActive = conv.participant.isActive
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = conv.participant.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (conv.participant.isActive) "Online" else "Last seen ${conv.participant.lastSeenAt}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showStickerSheet = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Stickers")
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Message ${conv.participant.displayName.split(" ").first()}...") },
                    modifier = Modifier.weight(1f).testTag("dm_input"),
                    shape = MaterialTheme.shapes.medium,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendDirectMessage(conv.id, inputText)
                            inputText = ""
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
                        .background(if (inputText.isNotBlank()) PhchatLime else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("dm_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) PhchatNavy950 else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUser.id
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (isMe) PhchatLime else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                            if (msg.kind == MessageKind.STICKER) {
                                Text(text = msg.stickerEmoji ?: "🇵🇭", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(
                                text = msg.body,
                                color = if (isMe) PhchatNavy950 else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                            Text(
                                text = msg.timestamp,
                                color = if (isMe) PhchatNavy950.copy(alpha = 0.72f) else MaterialTheme.colorScheme.outline,
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showStickerSheet) {
        StickerPickerSheet(
            onDismiss = { showStickerSheet = false },
            onSelectSticker = { sticker ->
                viewModel.sendDirectMessage(conv.id, "${sticker.emoji} ${sticker.tagalogPhrase}")
            }
        )
    }
}
