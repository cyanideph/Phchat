package com.example.phchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.phchat.ui.components.Phchat3dIcon
import com.example.phchat.ui.components.Phchat3dIconView
import com.example.phchat.ui.theme.PhGoldContainer
import com.example.phchat.ui.theme.PhRedSecondary

private data class OnboardingPage(val icon: Phchat3dIcon, val title: String, val body: String)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pages = listOf(
        OnboardingPage(Phchat3dIcon.CHAT, "Welcome to Phchat", "A social messaging space for Filipino conversations, communities, and tambayan."),
        OnboardingPage(Phchat3dIcon.BOY, "Chat. Connect. Tambay.", "Message friends, meet people, and keep the conversation moving."),
        OnboardingPage(Phchat3dIcon.MOBILE, "Find your community", "Discover rooms, local communities, posts, and shared interests."),
        OnboardingPage(Phchat3dIcon.LOCK, "Your space, your control", "Manage your profile, privacy, notifications, blocking, and community interactions.")
    )
    var pageIndex by remember { mutableIntStateOf(0) }
    val page = pages[pageIndex]

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("PHCHAT", fontWeight = FontWeight.Black)
                TextButton(onClick = onComplete) { Text("Skip") }
            }
            Spacer(Modifier.height(42.dp))
            Box(Modifier.size(230.dp), Alignment.Center) {
                Surface(
                    Modifier.size(210.dp),
                    CircleShape,
                    color = if (pageIndex == 2) PhRedSecondary.copy(alpha = .10f) else PhGoldContainer.copy(alpha = .10f)
                ) {}
                Phchat3dIconView(page.icon, size = 170.dp)
            }
            Spacer(Modifier.height(34.dp))
            Text(page.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(14.dp))
            Text(page.body, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                pages.indices.forEach { index ->
                    Box(
                        Modifier
                            .size(if (index == pageIndex) 24.dp else 7.dp, 7.dp)
                            .background(
                                if (index == pageIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = { if (pageIndex == pages.lastIndex) onComplete() else pageIndex++ },
                Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (pageIndex == pages.lastIndex) "Get started" else "Continue", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text("\${pageIndex + 1} / \${pages.size}", color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.labelSmall)
        }
    }
}
