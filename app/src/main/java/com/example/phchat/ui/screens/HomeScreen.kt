package com.example.phchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Feed
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
import com.example.phchat.ui.theme.*
import com.example.phchat.viewmodel.PhchatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PhchatViewModel,
    initialTab: Int = 0,
    onOpenNotifications: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🇵🇭 Phchat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = PhRedSecondary,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "PINOY",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = PhRedSecondary) {
                                        Text("$unreadCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
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
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Tambayan") },
                    label = { Text("Tambayan") },
                    modifier = Modifier.testTag("tab_tambayan")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Forum, contentDescription = "Chika") },
                    label = { Text("Chika") },
                    modifier = Modifier.testTag("tab_chika")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.AutoMirrored.Filled.Feed, contentDescription = "Community") },
                    label = { Text("Community") },
                    modifier = Modifier.testTag("tab_community")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Hall") },
                    label = { Text("Tambay Hall") },
                    modifier = Modifier.testTag("tab_hall")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    modifier = Modifier.testTag("tab_profile")
                )
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    ExtendedFloatingActionButton(
                        onClick = { showCreateRoomDialog = true },
                        icon = { Icon(Icons.Default.Add, contentDescription = "New Room") },
                        text = { Text("New Room") },
                        containerColor = PhBluePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("create_room_fab")
                    )
                }
                2 -> {
                    ExtendedFloatingActionButton(
                        onClick = { showCreatePostDialog = true },
                        icon = { Icon(Icons.Default.Edit, contentDescription = "Post") },
                        text = { Text("Post / Poll") },
                        containerColor = PhBluePrimary,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("create_post_fab")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TambayanTab(viewModel = viewModel)
                1 -> ChikaTab(viewModel = viewModel)
                2 -> CommunityTab(viewModel = viewModel)
                3 -> TambayHallTab(viewModel = viewModel)
                4 -> MyProfileTab(viewModel = viewModel)
            }
        }
    }

    if (showCreateRoomDialog) {
        CreateRoomDialog(
            onDismiss = { showCreateRoomDialog = false },
            onCreate = { name, code, provName, announcement ->
                val newRoom = viewModel.createRoom(name, code, provName, announcement)
                showCreateRoomDialog = false
                viewModel.openRoom(newRoom.id)
            }
        )
    }

    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onCreate = { title, body, category, pollOptions ->
                viewModel.createPost(title, body, category, pollOptions)
                showCreatePostDialog = false
            }
        )
    }
}

// ---------------- TAB 0: Tambayan (Rooms) ----------------
@Composable
fun TambayanTab(viewModel: PhchatViewModel) {
    val rooms by viewModel.filteredRooms.collectAsState()
    val selectedProvince by viewModel.selectedProvinceFilter.collectAsState()
    var searchInput by remember { mutableStateOf("") }

    val provinces = listOf(
        "ALL" to "All Philippines 🇵🇭",
        "NCR" to "Metro Manila",
        "CEB" to "Cebu",
        "DVO" to "Davao",
        "PAM" to "Pampanga"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Search field
            OutlinedTextField(
                value = searchInput,
                onValueChange = {
                    searchInput = it
                    viewModel.searchQuery.value = it
                },
                placeholder = { Text("Search rooms by name, province, topic...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_rooms_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            // Province filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(provinces) { (code, label) ->
                    FilterChip(
                        selected = selectedProvince == code,
                        onClick = { viewModel.selectedProvinceFilter.value = code },
                        label = { Text(label) },
                        leadingIcon = if (selectedProvince == code) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Tambayan Rooms (${rooms.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (rooms.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🏝️", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Walang nahanap na room",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Mag-create ng bagong room para sa iyong probinsya o barkada!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(rooms) { room ->
                RoomCard(
                    room = room,
                    onClick = { viewModel.openRoom(room.id) },
                    onTogglePin = { viewModel.toggleRoomPinned(room.id) },
                    onToggleJoin = { viewModel.toggleJoinRoom(room.id) }
                )
            }
        }
    }
}

@Composable
fun RoomCard(
    room: Room,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleJoin: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("room_card_${room.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(room.colorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = room.name.take(2),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ProvinceBadge(code = room.provinceCode, name = room.provinceName)
                            RoleBadge(role = room.myRole)
                        }
                    }
                }

                IconButton(onClick = onTogglePin) {
                    Icon(
                        imageVector = if (room.isPinned) Icons.Default.PushPin else Icons.Default.BookmarkBorder,
                        contentDescription = "Pin Room",
                        tint = if (room.isPinned) PhRedSecondary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            if (room.announcement.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📢 " + room.announcement,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "${room.onlineCount} online",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = StatusOnline
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${room.memberCount} members",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                FilledTonalButton(
                    onClick = onToggleJoin,
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (room.isJoined) PhBlueContainer else MaterialTheme.colorScheme.primary,
                        contentColor = if (room.isJoined) PhOnBlueContainer else Color.White
                    )
                ) {
                    Text(
                        text = if (room.isJoined) "Joined ✓" else "Join",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ---------------- TAB 1: Chika (DMs) ----------------
@Composable
fun ChikaTab(viewModel: PhchatViewModel) {
    val conversations by viewModel.conversations.collectAsState()
    val profiles by viewModel.profiles.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Online Tambay Buddies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(profiles.filter { it.id != "usr_me" }) { user ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { viewModel.startConversationWithUser(user) }
                            .width(64.dp)
                    ) {
                        UserAvatar(
                            initial = user.avatarInitial,
                            colorHex = user.avatarColorHex,
                            size = 52.dp,
                            isActive = user.isActive
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.displayName.split(" ").first(),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Direct Messages",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(conversations) { conv ->
            Card(
                onClick = { viewModel.openDirectChat(conv.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().testTag("conv_${conv.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        initial = conv.participant.avatarInitial,
                        colorHex = conv.participant.avatarColorHex,
                        size = 46.dp,
                        isActive = conv.participant.isActive
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = conv.participant.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = conv.lastMessageTime,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = conv.lastMessage,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            if (conv.unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(PhRedSecondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${conv.unreadCount}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TAB 2: Community Feed & Polls ----------------
@Composable
fun CommunityTab(viewModel: PhchatViewModel) {
    val posts by viewModel.contentPosts.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "General", "Provincial Buzz", "Food & Travel", "Gaming")

    val filteredPosts = if (selectedCategory == "All") posts else posts.filter { it.category == selectedCategory }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }
        }

        items(filteredPosts) { post ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth().testTag("post_${post.id}")
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    // Author Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            initial = post.author.avatarInitial,
                            colorHex = post.author.avatarColorHex,
                            size = 38.dp,
                            onClick = { viewModel.openProfile(post.author.id) }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = post.author.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${post.category} • ${post.createdAt}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = post.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = post.body,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Interactive Poll
                    if (post.poll != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                Text(
                                    text = "📊 " + post.poll.question,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                post.poll.options.forEach { option ->
                                    val percentage = if (post.poll.totalVotes > 0) {
                                        (option.votes.toFloat() / post.poll.totalVotes * 100).toInt()
                                    } else 0
                                    val isSelected = post.poll.selectedOptionId == option.id

                                    Card(
                                        onClick = { viewModel.votePoll(post.id, option.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) PhBlueContainer else MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = option.text,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    text = "$percentage% (${option.votes})",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            LinearProgressIndicator(
                                                progress = { if (post.poll.totalVotes > 0) option.votes.toFloat() / post.poll.totalVotes else 0f },
                                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                                color = if (isSelected) PhBluePrimary else PhYellowSun,
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${post.poll.totalVotes} total votes",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.togglePostLike(post.id) }) {
                                Icon(
                                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (post.isLiked) PhRedSecondary else MaterialTheme.colorScheme.outline
                                )
                            }
                            Text(
                                text = "${post.likesCount}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${post.commentsCount}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        IconButton(onClick = { viewModel.togglePostSave(post.id) }) {
                            Icon(
                                imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (post.isSaved) PhBluePrimary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TAB 3: Tambay Hall (Leaderboard & Check-in) ----------------
@Composable
fun TambayHallTab(viewModel: PhchatViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    val hasCheckedIn by viewModel.hasCheckedInToday.collectAsState()

    // Sorted leaderboard
    val leaderboard = profiles.sortedByDescending { it.points }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Daily Streak Card
            StreakPointsCard(
                streak = currentUser.streak,
                points = currentUser.points,
                hasCheckedIn = hasCheckedIn,
                onCheckInClick = { viewModel.checkIn() }
            )
        }

        item {
            Text(
                text = "⭐ Featured Pinoy Tambay",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(profiles.filter { it.isFeatured }) { p ->
                    Card(
                        onClick = { viewModel.openProfile(p.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.width(140.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            UserAvatar(
                                initial = p.avatarInitial,
                                colorHex = p.avatarColorHex,
                                size = 48.dp,
                                isActive = p.isActive
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = p.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = p.province,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = PhGoldContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🔥 ${p.streak}d streak",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PhOnGoldContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "🏆 Top Tambay Leaderboard",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(leaderboard.mapIndexed { index, p -> Pair(index + 1, p) }) { (rank, p) ->
            Card(
                onClick = { viewModel.openProfile(p.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (p.id == currentUser.id) PhBlueContainer else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val medalEmoji = when (rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#$rank"
                    }
                    Text(
                        text = medalEmoji,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(36.dp)
                    )
                    UserAvatar(
                        initial = p.avatarInitial,
                        colorHex = p.avatarColorHex,
                        size = 40.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = p.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (p.id == currentUser.id) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(You)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PhBluePrimary
                                )
                            }
                        }
                        Text(
                            text = p.statusText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${p.points} pts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PhBluePrimary
                        )
                        Text(
                            text = "${p.streak} days",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

// ---------------- TAB 4: My Profile & Wall ----------------
@Composable
fun MyProfileTab(viewModel: PhchatViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val commentsMap by viewModel.profileComments.collectAsState()
    val visitsMap by viewModel.profileVisits.collectAsState()

    val myComments = commentsMap[currentUser.id] ?: emptyList()
    val myVisits = visitsMap[currentUser.id] ?: emptyList()

    var showEditStatusDialog by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UserAvatar(
                        initial = currentUser.avatarInitial,
                        colorHex = currentUser.avatarColorHex,
                        size = 68.dp,
                        isActive = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentUser.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "@${currentUser.username} • ${currentUser.province}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { showEditStatusDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "💬 " + currentUser.statusText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Status",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentUser.bio,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${currentUser.points}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Points", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${currentUser.streak} days", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${myVisits.size}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "Visits", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }

        // Profile Visits (Who visited my profile)
        if (myVisits.isNotEmpty()) {
            item {
                Text(
                    text = "👀 Recent Profile Visitors",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(myVisits) { visit ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserAvatar(
                                    initial = visit.visitor.avatarInitial,
                                    colorHex = visit.visitor.avatarColorHex,
                                    size = 28.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = visit.visitor.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = visit.visitedAt, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Profile Wall / Comments
        item {
            Text(
                text = "📝 Guestbook & Profile Wall",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Write a message on wall (1-4000 chars)...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            viewModel.addProfileComment(currentUser.id, newCommentText)
                            newCommentText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PhBluePrimary)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Post Comment",
                        tint = Color.White
                    )
                }
            }
        }

        items(myComments) { comment ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            initial = comment.author.avatarInitial,
                            colorHex = comment.author.avatarColorHex,
                            size = 32.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = comment.author.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = comment.createdAt, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        // Vote buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.voteProfileComment(currentUser.id, comment.id, 1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Upvote",
                                    tint = if (comment.userVote == 1) PhBluePrimary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "${comment.votes}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { viewModel.voteProfileComment(currentUser.id, comment.id, -1) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbDown,
                                    contentDescription = "Downvote",
                                    tint = if (comment.userVote == -1) PhRedSecondary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = comment.body,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showEditStatusDialog) {
        var statusInput by remember { mutableStateOf(currentUser.statusText) }
        val statusPresets = listOf(
            "Online & Tambay ☕",
            "Kape Muna Tayo ☕",
            "Chika Time 👀",
            "Busy sa Work 💻",
            "Kumakain ng Lechon 🐷",
            "Looking for Tambay Friends 🇵🇭"
        )
        AlertDialog(
            onDismissRequest = { showEditStatusDialog = false },
            title = { Text("Update Tambay Status") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = statusInput,
                        onValueChange = { statusInput = it },
                        label = { Text("Custom status") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Quick Presets:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    statusPresets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { statusInput = preset }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateStatus(statusInput)
                    showEditStatusDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ---------------- Dialogs ----------------
@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, code: String, provName: String, announcement: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var provinceCode by remember { mutableStateOf("NCR") }
    var provinceName by remember { mutableStateOf("Metro Manila") }
    var announcement by remember { mutableStateOf("") }

    val provinces = listOf(
        "NCR" to "Metro Manila",
        "CEB" to "Cebu",
        "DVO" to "Davao",
        "PAM" to "Pampanga",
        "ILO" to "Iloilo",
        "ALL" to "All Philippines"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Tambayan Room") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Room Name (e.g. Batangas Chill)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Province / Location:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(provinces) { (code, prov) ->
                        FilterChip(
                            selected = provinceCode == code,
                            onClick = {
                                provinceCode = code
                                provinceName = prov
                            },
                            label = { Text(code) }
                        )
                    }
                }

                OutlinedTextField(
                    value = announcement,
                    onValueChange = { announcement = it },
                    label = { Text("Room Announcement / Rules") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, provinceCode, provinceName, announcement)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create Room")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, body: String, category: String, pollOptions: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var isPoll by remember { mutableStateOf(false) }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }

    val categories = listOf("General", "Provincial Buzz", "Food & Travel", "Gaming", "Chismis")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isPoll) "Create Community Poll" else "Create Community Post") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = !isPoll,
                        onClick = { isPoll = false },
                        label = { Text("Regular Post") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = isPoll,
                        onClick = { isPoll = true },
                        label = { Text("📊 Poll") }
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isPoll) "Poll Question" else "Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Description / Details") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (isPoll) {
                    OutlinedTextField(
                        value = option1,
                        onValueChange = { option1 = it },
                        label = { Text("Option 1") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = option2,
                        onValueChange = { option2 = it },
                        label = { Text("Option 2") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val pollList = if (isPoll && option1.isNotBlank() && option2.isNotBlank()) {
                            listOf(option1, option2)
                        } else emptyList()
                        onCreate(title, body, category, pollList)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Publish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
