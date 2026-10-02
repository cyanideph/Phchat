package com.example.phchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import android.content.Context
import androidx.compose.ui.Modifier
import com.example.phchat.ui.screens.*
import com.example.phchat.ui.theme.PhchatTheme
import com.example.phchat.viewmodel.PhchatViewModel
import com.example.phchat.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: PhchatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = remember {
                getSharedPreferences("phchat_preferences", Context.MODE_PRIVATE)
            }
            var darkTheme by remember {
                mutableStateOf(
                    if (prefs.contains("dark_theme")) prefs.getBoolean("dark_theme", false)
                    else androidx.compose.foundation.isSystemInDarkTheme()
                )
            }

            PhchatTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    var showNotificationsSheet by remember { mutableStateOf(false) }

                    when (val screen = currentScreen) {
                        is Screen.Auth -> {
                            AuthScreen(
                                viewModel = viewModel,
                                onSuccess = { viewModel.navigateTo(Screen.Home(0)) }
                            )
                        }
                        is Screen.Home -> {
                            HomeScreen(
                                viewModel = viewModel,
                                initialTab = screen.initialTab,
                                onOpenNotifications = { showNotificationsSheet = true }
                            )
                        }
                        is Screen.RoomChat -> {
                            RoomChatScreen(
                                roomId = screen.roomId,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                        }
                        is Screen.DirectChat -> {
                            DirectChatScreen(
                                conversationId = screen.conversationId,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                        }
                        is Screen.ProfileDetail -> {
                            ProfileScreen(
                                profileId = screen.profileId,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                        }
                        is Screen.Settings -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                darkTheme = darkTheme,
                                onThemeChange = { enabled ->
                                    darkTheme = enabled
                                    prefs.edit().putBoolean("dark_theme", enabled).apply()
                                },
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                        }
                    }

                    if (showNotificationsSheet) {
                        NotificationsSheet(
                            viewModel = viewModel,
                            onDismiss = { showNotificationsSheet = false },
                            onOpenRoom = { roomId ->
                                showNotificationsSheet = false
                                viewModel.openRoom(roomId)
                            }
                        )
                    }
                }
            }
        }
    }
}
