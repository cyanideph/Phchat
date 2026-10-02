package com.example.phchat.ui.theme

import androidx.compose.ui.graphics.Color

// PHchat "Soft Pro" theme inspired by the supplied Uzzap showcase.
// Both modes share one brand language: deep blue-black surfaces, vivid community green,
// white/mist surfaces, restrained blue/red accents, and soft borders.

val PhBluePrimary = Color(0xFF00A94F)      // Soft Pro community green
val PhBlueDark = Color(0xFF06161E)         // Deep blue-black header / dark background
val PhBlueLight = Color(0xFF16C96A)        // Brighter active green
val PhBlueContainer = Color(0xFFE7F7EE)    // Light green selection surface
val PhOnBlueContainer = Color(0xFF07351F)

val PhRedSecondary = Color(0xFFD84B4B)     // Restrained alert/red accent
val PhRedContainer = Color(0xFFFBE9E9)
val PhOnRedContainer = Color(0xFF641B1B)

val PhYellowSun = Color(0xFFF4C84B)        // Warm secondary highlight
val PhGoldContainer = Color(0xFFFFF5CF)
val PhOnGoldContainer = Color(0xFF4D3B00)

val SurfaceLight = Color(0xFFF3F7F8)       // Soft cool-white app background
val SurfaceVariantLight = Color(0xFFE8EFF2) // Soft input/chip surface
val OnSurfaceLight = Color(0xFF14252D)     // Deep blue-black text
val OutlineLight = Color(0xFFD4E0E5)       // Soft Pro border

val SurfaceDark = Color(0xFF06161E)        // Screenshot-style navy/black background
val SurfaceVariantDark = Color(0xFF0C222D) // Elevated dark cards
val OnSurfaceDark = Color(0xFFF2F7F8)      // Primary dark-mode text
val OutlineDark = Color(0xFF24404B)        // Subtle dark border

val StatusOnline = Color(0xFF20D56F)       // Bright online indicator
val StatusBusy = Color(0xFFD84B4B)
val StatusAway = Color(0xFFF0B83D)

// Regional accents remain secondary to the green Soft Pro brand.
val RegionNcr = Color(0xFF2476A8)
val RegionVisayas = Color(0xFFB87924)
val RegionMindanao = Color(0xFF008C68)
val RegionLuzon = Color(0xFF765A91)
val RegionBicol = Color(0xFFD84B4B)

// Roles
val RoleOwner = Color(0xFFD84B4B)
val RoleAdmin = Color(0xFF00A94F)
val RoleMod = Color(0xFF008C68)
val RoleMember = Color(0xFF60757E)
