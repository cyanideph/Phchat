package com.example.phchat.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.phchat.model.MemberRole
import com.example.phchat.model.Profile
import com.example.phchat.ui.components.RoleBadge
import com.example.phchat.ui.components.UserAvatar
import com.example.phchat.ui.theme.*

@Composable
fun ReportDialog(
    targetName: String,
    onDismiss: () -> Unit,
    onSubmitReport: (reason: String, details: String) -> Unit
) {
    val reportReasons = listOf(
        "Spam o paulit-ulit na mensahe",
        "Pang-aabuso o panliligalig (Harassment)",
        "Bastos o malaswang nilalaman",
        "Pang-iinsulto o hate speech",
        "Scam o ilegal na aktibidad",
        "Iba pang paglabag sa alituntunin"
    )

    var selectedReason by remember { mutableStateOf(reportReasons[0]) }
    var detailsInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = PhRedSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("I-Report si $targetName", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Piliin ang dahilan ng pag-report ayon sa Community Guidelines:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                reportReasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 3.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = PhRedSecondary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = reason, fontSize = 12.sp)
                    }
                }

                OutlinedTextField(
                    value = detailsInput,
                    onValueChange = { detailsInput = it },
                    label = { Text("Karagdagang paliwanag (opsyonal)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSubmitting = true
                    onSubmitReport(selectedReason, detailsInput)
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = PhRedSecondary)
            ) {
                Text(if (isSubmitting) "Ipinapadala..." else "Isumite ang Report", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kanselahin")
            }
        }
    )
}

@Composable
fun BlockUserDialog(
    userName: String,
    onDismiss: () -> Unit,
    onConfirmBlock: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Block, contentDescription = null, tint = PhRedSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("I-Block si $userName?", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                text = "Hindi mo na makikita ang mga mensahe mula kay $userName sa alinmang tambayan o pribadong usapan. Maaari mo itong bawiin sa iyong mga setting.",
                fontSize = 13.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmBlock,
                colors = ButtonDefaults.buttonColors(containerColor = PhRedSecondary)
            ) {
                Text("I-Block", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huwag muna")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomMemberDirectorySheet(
    roomName: String,
    members: List<Profile>,
    currentUserId: String,
    currentUserRole: MemberRole,
    onDismiss: () -> Unit,
    onWhisper: (Profile) -> Unit,
    onViewProfile: (Profile) -> Unit,
    onAddBuddy: (Profile) -> Unit,
    onReportUser: (Profile) -> Unit,
    onBlockUser: (Profile) -> Unit,
    onStrikeUser: ((Profile) -> Unit)? = null
) {
    var selectedMemberForActions by remember { mutableStateOf<Profile?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "👥 Mga Tambay sa Loob",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PhBluePrimary
                    )
                    Text(
                        text = "$roomName • ${members.size} Kasama",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                items(members) { member ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMemberForActions = member },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UserAvatar(
                                    initial = member.displayName,
                                    colorHex = member.avatarColorHex,
                                    size = 40.dp,
                                    isActive = member.isActive
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = member.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        if (member.id == currentUserId) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("(Ikaw)", fontSize = 11.sp, color = PhBlueLight)
                                        }
                                    }
                                    Text(
                                        text = "@${member.username} • 📍 ${member.province}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Actions",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }

    // Member Action Options Dialog
    val activeMember = selectedMemberForActions
    if (activeMember != null) {
        val member = activeMember
        AlertDialog(
            onDismissRequest = { selectedMemberForActions = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(initial = member.displayName, colorHex = member.avatarColorHex, size = 36.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(member.displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("@${member.username}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = {
                            onWhisper(member)
                            selectedMemberForActions = null
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bulong / Pribadong Mensahe (PM)")
                    }

                    FilledTonalButton(
                        onClick = {
                            onViewProfile(member)
                            selectedMemberForActions = null
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tingnan ang Profile")
                    }

                    FilledTonalButton(
                        onClick = {
                            onAddBuddy(member)
                            selectedMemberForActions = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Idagdag sa Barkada (Buddy)")
                    }

                    if (currentUserRole == MemberRole.OWNER || currentUserRole == MemberRole.ADMIN) {
                        OutlinedButton(
                            onClick = {
                                onStrikeUser?.invoke(member)
                                selectedMemberForActions = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PhYellowSun)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bigyan ng Warning / Strike")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            onBlockUser(member)
                            selectedMemberForActions = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PhRedSecondary)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("I-Block ang User")
                    }

                    TextButton(
                        onClick = {
                            onReportUser(member)
                            selectedMemberForActions = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = PhRedSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("I-Report sa Moderation", color = PhRedSecondary)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedMemberForActions = null }) {
                    Text("Isara")
                }
            }
        )
    }
}
