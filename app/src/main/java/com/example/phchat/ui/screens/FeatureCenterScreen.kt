package com.example.phchat.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.phchat.viewmodel.PhchatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCenterScreen(viewModel: PhchatViewModel, onNavigateBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var searchKind by remember { mutableStateOf("profiles") }
    var userId by remember { mutableStateOf("") }
    var roomId by remember { mutableStateOf("") }
    var conversationId by remember { mutableStateOf("") }
    var inviteId by remember { mutableStateOf("") }
    var requestId by remember { mutableStateOf("") }
    var messageId by remember { mutableStateOf("") }
    var contentId by remember { mutableStateOf("") }
    var commentId by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30") }
    var commentBody by remember { mutableStateOf("") }
    val results by viewModel.featureResults.collectAsState()

    val context = LocalContext.current
    val mediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadFeatureMedia(context, it, roomId = roomId.takeIf { value -> value.isNotBlank() }) }
    }

    fun submit(action: () -> Unit) { action() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PHChat Feature Center") },
                navigationIcon = { TextButton(onClick = onNavigateBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Everything the backend exposes should have a reachable Android action.", style = MaterialTheme.typography.bodyMedium)

            FeatureCard("Discovery & Search") {
                OutlinedTextField(query, { query = it }, label = { Text("Search") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("profiles","rooms","content","chats").forEach { kind ->
                        FilterChip(selected = searchKind == kind, onClick = { searchKind = kind }, label = { Text(kind) })
                    }
                }
                Button(onClick = { submit { viewModel.searchFeature(searchKind, query) } }) { Text("Search backend") }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = { viewModel.loadOnlineUsers() }) { Text("Online users") }
                    OutlinedButton(onClick = { if (roomId.isNotBlank()) viewModel.loadOnlineRoomMembers(roomId) }) { Text("Room online") }
                }
            }

            FeatureCard("Favorites & Social Graph") {
                OutlinedTextField(userId, { userId = it }, label = { Text("User UUID") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.toggleFavoriteUser(userId) }) { Text("Favorite") }
                    OutlinedButton(onClick = { viewModel.loadFavorites() }) { Text("Favorites") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(onClick = { viewModel.loadFollowers(userId) }) { Text("Followers") }
                    OutlinedButton(onClick = { viewModel.loadFollowing(userId) }) { Text("Following") }
                    OutlinedButton(onClick = { viewModel.recordProfileVisit(userId) }) { Text("Visit") }
                }
                OutlinedButton(onClick = { viewModel.loadProfileVisitors() }) { Text("Profile visitors") }
            }

            FeatureCard("Room & DM Invites") {
                OutlinedTextField(roomId, { roomId = it }, label = { Text("Room UUID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(conversationId, { conversationId = it }, label = { Text("Conversation UUID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(userId, { userId = it }, label = { Text("Invitee UUID") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.createRoomInvite(roomId, userId) }) { Text("Invite to room") }
                    Button(onClick = { viewModel.createConversationInvite(conversationId, userId) }) { Text("Invite to DM") }
                }
                OutlinedTextField(inviteId, { inviteId = it }, label = { Text("Invite UUID") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.respondRoomInvite(inviteId, true) }) { Text("Accept room") }
                    OutlinedButton(onClick = { viewModel.respondRoomInvite(inviteId, false) }) { Text("Decline room") }
                    OutlinedButton(onClick = { viewModel.respondConversationInvite(inviteId, true) }) { Text("Accept DM") }
                }
            }

            FeatureCard("Room Co-host & Moderation") {
                OutlinedTextField(requestId, { requestId = it }, label = { Text("Co-host request UUID") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.requestCoHost(roomId, userId) }) { Text("Request co-host") }
                    OutlinedButton(onClick = { viewModel.respondCoHostRequest(requestId, true) }) { Text("Accept") }
                    OutlinedButton(onClick = { viewModel.respondCoHostRequest(requestId, false) }) { Text("Decline") }
                    OutlinedButton(onClick = { viewModel.cancelCoHostRequest(requestId) }) { Text("Cancel") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.setCoHost(roomId, userId, true) }) { Text("Make co-host") }
                    OutlinedButton(onClick = { viewModel.setCoHost(roomId, userId, false) }) { Text("Remove co-host") }
                }
                OutlinedTextField(duration, { duration = it.filter(Char::isDigit) }, label = { Text("Duration minutes") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(reason, { reason = it }, label = { Text("Reason") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.strikeMember(roomId, userId, duration.toIntOrNull() ?: 30, reason) }) { Text("Strike") }
                    OutlinedButton(onClick = { viewModel.kickMember(roomId, userId, true, reason) }) { Text("Kick") }
                    OutlinedButton(onClick = { viewModel.moderateMember(roomId, userId, "mute", duration.toIntOrNull() ?: 30, reason) }) { Text("Mute") }
                    OutlinedButton(onClick = { viewModel.moderateMember(roomId, userId, "ban", duration.toIntOrNull() ?: 30, reason) }) { Text("Ban") }
                }
                OutlinedTextField(messageId, { messageId = it }, label = { Text("Message UUID") }, modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = { viewModel.moderateMessage(messageId, "delete", reason) }) { Text("Moderate message") }
            }

            FeatureCard("Community Content") {
                OutlinedTextField(contentId, { contentId = it }, label = { Text("Post UUID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(commentBody, { commentBody = it }, label = { Text("Comment") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.addContentComment(contentId, commentBody) }) { Text("Comment") }
                    OutlinedButton(onClick = { viewModel.repostContent(contentId) }) { Text("Repost") }
                }
                OutlinedTextField(commentId, { commentId = it }, label = { Text("Comment UUID") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.voteContentComment(commentId, 1) }) { Text("Upvote") }
                    OutlinedButton(onClick = { viewModel.voteContentComment(commentId, -1) }) { Text("Downvote") }
                }
            }

            FeatureCard("Media") {
                Button(onClick = { mediaPicker.launch("*/*") }) { Text("Attach / upload media") }
                Text("Uploads use Supabase Storage plus the media metadata contract.")
            }

            FeatureCard("Notifications") {
                OutlinedTextField(inviteId, { inviteId = it }, label = { Text("Notification UUID") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { viewModel.markNotificationRead(inviteId) }) { Text("Mark read") }
                    OutlinedButton(onClick = { viewModel.deleteNotification(inviteId) }) { Text("Delete") }
                    OutlinedButton(onClick = { viewModel.clearNotifications() }) { Text("Clear all") }
                }
            }

            FeatureCard("Execution log") {
                results.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
                if (results.isEmpty()) Text("No operations executed yet.")
            }
        }
    }
}

@Composable
private fun FeatureCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}
