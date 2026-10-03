package com.example.phchat.ui.theme

import androidx.compose.ui.graphics.Color

// PHchat Premium Social: black-first, warm-neutral light mode, restrained gold accent.
// Keep the existing semantic names so screen code remains source-compatible.

val PhBluePrimary = Color(0xFF0A0A0D)      // Primary action / black
val PhBlueDark = Color(0xFF050505)         // Near-black foundation
val PhBlueLight = Color(0xFF17171A)        // Elevated dark action
val PhBlueContainer = Color(0xFFECECE9)    // Soft neutral selection
val PhOnBlueContainer = Color(0xFF0A0A0D)

val PhRedSecondary = Color(0xFFC84B4B)
val PhRedContainer = Color(0xFFF7E9E9)
val PhOnRedContainer = Color(0xFF5D1818)

val PhYellowSun = Color(0xFFF5AD2A)        // Restrained warm gold
val PhGoldContainer = Color(0xFFFFF2D6)
val PhOnGoldContainer = Color(0xFF4A3000)

val SurfaceLight = Color(0xFFF7F7F5)       // Warm near-white canvas
val SurfaceVariantLight = Color(0xFFEEEEEB) // Soft neutral card/input surface
val OnSurfaceLight = Color(0xFF0A0A0D)     // Near-black text
val OutlineLight = Color(0xFFDCDCD8)       // Quiet neutral border

val SurfaceDark = Color(0xFF050505)        // Near-black foundation
val SurfaceVariantDark = Color(0xFF17171A) // Elevated charcoal surface
val OnSurfaceDark = Color(0xFFF7F7F5)      // Soft white text
val OutlineDark = Color(0xFF2A2A2D)        // Subtle dark border

val StatusOnline = Color(0xFF32C776)
val StatusBusy = Color(0xFFC84B4B)
val StatusAway = Color(0xFFE7A83B)

// Regional accents stay secondary and never define the core theme.
val RegionNcr = Color(0xFF5C7A91)
val RegionVisayas = Color(0xFFB87924)
val RegionMindanao = Color(0xFF4B8C78)
val RegionLuzon = Color(0xFF766A86)
val RegionBicol = Color(0xFFC84B4B)

// Roles
val RoleOwner = Color(0xFFC84B4B)
val RoleAdmin = Color(0xFF5B6B7A)
val RoleMod = Color(0xFF6D8B7D)
val RoleMember = Color(0xFF666666)
