package com.example.phchat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phchat.model.PhilippineAdministrativeDivisions
import com.example.phchat.model.PhilippineRegion
import com.example.phchat.model.Province
import com.example.phchat.model.Room
import com.example.phchat.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionProvinceExplorerSheet(
    rooms: List<Room>,
    onDismiss: () -> Unit,
    onSelectRoom: (Room) -> Unit,
    onCreateRoomInProvince: (Province) -> Unit
) {
    var expandedRegionCode by remember { mutableStateOf<String?>("NCR") }
    var selectedProvince by remember { mutableStateOf<Province?>(null) }

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
                        text = "🗺️ Pambansang Talaan ng Tambayan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PhAcidLime
                    )
                    Text(
                        text = "17 Rehiyon • 81 Lalawigan ng Pilipinas",
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
                    .heightIn(max = 500.dp)
            ) {
                items(PhilippineAdministrativeDivisions.regions) { region ->
                    val isExpanded = expandedRegionCode == region.code

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) PhAcidLimeContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedRegionCode = if (isExpanded) null else region.code
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = region.culturalGlyph, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = region.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isExpanded) PhBlack else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${region.islandGroup} • ${region.provinces.size} Provinces/Cities",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = if (isExpanded) PhAcidLime else MaterialTheme.colorScheme.outline
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    region.provinces.forEach { province ->
                                        val provRooms = rooms.filter {
                                            it.provinceCode.equals(province.code, ignoreCase = true) ||
                                            it.provinceName.contains(province.name, ignoreCase = true)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedProvince = province }
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(province.culturalGlyph, fontSize = 16.sp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(
                                                            text = province.name,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 13.sp
                                                        )
                                                        Text(
                                                            text = "Kabisera: ${province.capitalOrCenter}",
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    color = if (provRooms.isNotEmpty()) StatusOnline.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        text = if (provRooms.isNotEmpty()) "${provRooms.size} Rooms" else "Bago +",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (provRooms.isNotEmpty()) StatusOnline else MaterialTheme.colorScheme.outline,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
            }
        }
    }

    // Selected Province Rooms Sub-dialog
    if (selectedProvince != null) {
        val prov = selectedProvince!!
        val matchingRooms = rooms.filter {
            it.provinceCode.equals(prov.code, ignoreCase = true) ||
            it.provinceName.contains(prov.name, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = { selectedProvince = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(prov.culturalGlyph, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(prov.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (matchingRooms.isEmpty()) {
                        Text(
                            text = "Walang tambayan pa sa ${prov.name}. Ikaw ba ang magbubukas ng unang room para sa inyong lalawigan?",
                            fontSize = 13.sp
                        )
                    } else {
                        Text("Mga aktibong room:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        matchingRooms.forEach { room ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectRoom(room)
                                        selectedProvince = null
                                        onDismiss()
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(room.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("🟢 ${room.onlineCount}", fontSize = 11.sp, color = StatusOnline)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateRoomInProvince(prov)
                        selectedProvince = null
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PhRedSecondary)
                ) {
                    Text("Gawa ng Room Dito +", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProvince = null }) {
                    Text("Isara")
                }
            }
        )
    }
}
