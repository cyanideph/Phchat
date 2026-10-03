package com.example.phchat.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.phchat.data.AuthState
import com.example.phchat.ui.components.PhchatInlineBanner
import com.example.phchat.ui.components.PhchatSettingRow
import com.example.phchat.ui.components.PhchatSettingsGroup
import com.example.phchat.ui.theme.PhchatSpacing
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
    val blockedIds by viewModel.blockedUserIds.collectAsState()
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadNotificationPreferences()
        loaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
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
            contentPadding = PaddingValues(horizontal = PhchatSpacing.lg, vertical = PhchatSpacing.md),
            verticalArrangement = Arrangement.spacedBy(PhchatSpacing.xl)
        ) {
            item {
                PhchatSettingsGroup("Account") {
                    PhchatSettingRow(
                        icon = Icons.Default.Person,
                        title = user.displayName,
                        subtitle = "@" + user.username
                    )
                    PhchatSettingRow(
                        icon = Icons.Default.Security,
                        title = "Signed-in account",
                        subtitle = when (authState) {
                            is AuthState.Authenticated -> "Authenticated with Supabase"
                            else -> "Not signed in"
                        }
                    )
                }
            }

            item {
                PhchatSettingsGroup("Appearance") {
                    PhchatSettingRow(
                        icon = if (darkTheme) Icons.Default.DarkMode else Icons.Default.WbSunny,
                        title = "Dark mode",
                        subtitle = if (darkTheme) "Dark theme" else "Light theme",
                        trailing = {
                            Switch(checked = darkTheme, onCheckedChange = onThemeChange)
                        }
                    )
                }
            }

            item {
                PhchatSettingsGroup("Notifications") {
                    NotificationToggle("Follows", preferences.followEnabled, loaded) { viewModel.updateNotificationPreference("follow_enabled", it) }
                    NotificationToggle("Blocks", preferences.blockEnabled, loaded) { viewModel.updateNotificationPreference("block_enabled", it) }
                    NotificationToggle("Post comments", preferences.contentCommentEnabled, loaded) { viewModel.updateNotificationPreference("content_comment_enabled", it) }
                    NotificationToggle("Comment replies", preferences.commentReplyEnabled, loaded) { viewModel.updateNotificationPreference("comment_reply_enabled", it) }
                    NotificationToggle("Post reactions", preferences.contentReactionEnabled, loaded) { viewModel.updateNotificationPreference("content_reaction_enabled", it) }
                    NotificationToggle("Room reactions", preferences.roomMessageReactionEnabled, loaded) { viewModel.updateNotificationPreference("room_message_reaction_enabled", it) }
                    NotificationToggle("Profile comments", preferences.profileCommentEnabled, loaded) { viewModel.updateNotificationPreference("profile_comment_enabled", it) }
                    NotificationToggle("Mentions", preferences.mentionEnabled, loaded) { viewModel.updateNotificationPreference("mention_enabled", it) }
                    NotificationToggle("Room invites", preferences.roomInviteEnabled, loaded) { viewModel.updateNotificationPreference("conversation_invite_enabled", it) }
                    NotificationToggle("Direct-message invites", preferences.conversationInviteEnabled, loaded) { viewModel.updateNotificationPreference("conversation_invite_enabled", it) }
                }
            }

            item {
                PhchatInlineBanner(
                    title = "Advanced features",
                    message = "Invites, discovery, favorites, moderation, media and comments.",
                    actionLabel = "Open",
                    onAction = onOpenFeatureCenter
                )
            }

            item {
                PhchatSettingsGroup("Privacy & safety") {
                    PhchatSettingRow(
                        icon = Icons.Default.Lock,
                        title = "Blocked accounts",
                        subtitle = blockedIds.size.toString() + " blocked in this session"
                    )
                    PhchatSettingRow(
                        icon = Icons.Default.Security,
                        title = "Data protection",
                        subtitle = "Supabase Auth + row-level security"
                    )
                }
            }

            item {
                PhchatSettingsGroup("About") {
                    PhchatSettingRow(
                        icon = Icons.Default.Info,
                        title = "Phchat",
                        subtitle = "Community messaging • Android"
                    )
                }
            }

            item {
                PhchatInlineBanner(
                    title = "Sign out",
                    message = "End the current session.",
                    icon = Icons.Default.Logout,
                    tone = MaterialTheme.colorScheme.error,
                    actionLabel = "Sign out",
                    onAction = { viewModel.signOut() }
                )
            }
        }
    }
}

@Composable
private fun NotificationToggle(
    title: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    PhchatSettingRow(
        icon = Icons.Default.Notifications,
        title = title,
        subtitle = if (enabled) "Receive this notification type" else "Loading preferences…",
        trailing = {
            Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
        }
    )
}
