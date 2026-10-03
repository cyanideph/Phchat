package com.example.phchat.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phchat.data.AuthState
import com.example.phchat.viewmodel.PhchatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PhchatViewModel,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenFeatureCenter: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val preferences by viewModel.notificationPreferences.collectAsState()
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadNotificationPreferences()
        loaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingsSection("Account") {
                    SettingsInfoRow(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        title = user.displayName,
                        subtitle = "@${user.username}"
                    )
                    SettingsInfoRow(
                        icon = { Icon(Icons.Default.Security, contentDescription = null) },
                        title = "Signed-in account",
                        subtitle = when (authState) {
                            is AuthState.Authenticated -> "Authenticated with PHChat"
                            else -> "Not signed in"
                        }
                    )
                }
            }

            item {
                SettingsSection("Appearance") {
                    SettingsSwitchRow(
                        icon = {
                            Icon(
                                if (darkTheme) Icons.Default.DarkMode else Icons.Default.WbSunny,
                                contentDescription = null
                            )
                        },
                        title = "Dark mode",
                        subtitle = if (darkTheme) "Dark theme" else "Light theme",
                        checked = darkTheme,
                        onCheckedChange = onThemeChange
                    )
                }
            }

            item {
                SettingsSection("Notifications") {
                    NotificationToggle("Follows", preferences.followEnabled, loaded) { viewModel.updateNotificationPreference("follow_enabled", it) }
                    NotificationToggle("Blocks", preferences.blockEnabled, loaded) { viewModel.updateNotificationPreference("block_enabled", it) }
                    NotificationToggle("Post comments", preferences.contentCommentEnabled, loaded) { viewModel.updateNotificationPreference("content_comment_enabled", it) }
                    NotificationToggle("Comment replies", preferences.commentReplyEnabled, loaded) { viewModel.updateNotificationPreference("comment_reply_enabled", it) }
                    NotificationToggle("Post reactions", preferences.contentReactionEnabled, loaded) { viewModel.updateNotificationPreference("content_reaction_enabled", it) }
                    NotificationToggle("Room reactions", preferences.roomMessageReactionEnabled, loaded) { viewModel.updateNotificationPreference("room_message_reaction_enabled", it) }
                    NotificationToggle("Profile comments", preferences.profileCommentEnabled, loaded) { viewModel.updateNotificationPreference("profile_comment_enabled", it) }
                    NotificationToggle("Mentions", preferences.mentionEnabled, loaded) { viewModel.updateNotificationPreference("mention_enabled", it) }
                    NotificationToggle("Room invites", preferences.roomInviteEnabled, loaded) { viewModel.updateNotificationPreference("room_invite_enabled", it) }
                    NotificationToggle("Direct-message invites", preferences.conversationInviteEnabled, loaded) { viewModel.updateNotificationPreference("conversation_invite_enabled", it) }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Advanced features", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Invites, discovery, favorites, moderation, media, comments and more", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TextButton(onClick = onOpenFeatureCenter) { Text("Open") }
                    }
                }
            }

            item {
                SettingsSection("Privacy & safety") {
                    SettingsInfoRow(
                        icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        title = "Blocked accounts",
                        subtitle = "${viewModel.blockedUserIds.collectAsState().value.size} blocked in this session"
                    )
                    SettingsInfoRow(
                        icon = { Icon(Icons.Default.Security, contentDescription = null) },
                        title = "Data protection",
                        subtitle = "Supabase Auth + row-level security"
                    )
                }
            }

            item {
                SettingsSection("About") {
                    SettingsInfoRow(
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        title = "PHChat",
                        subtitle = "Community messaging • Android"
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.signOut() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Sign out", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Sign out", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.SemiBold)
                            Text("End the current Current session", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp))
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.fillMaxWidth()) { content() }
        }
    }
}

@Composable
private fun SettingsInfoRow(icon: @Composable () -> Unit, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        icon()
        Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingsSwitchRow(icon: @Composable () -> Unit, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        icon()
        Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun NotificationToggle(title: String, checked: Boolean, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    SettingsSwitchRow(
        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
        title = title,
        subtitle = if (enabled) "Receive this notification type" else "Loading preferences…",
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}
