package com.example.phchat.ui.theme

import androidx.compose.ui.graphics.Color

// PHchat Signal: graphite + warm ivory + electric lime. Distinctive, restrained, editorial.
// Avoids generic social-app blue gradients and keeps accent color intentional.

val PhBluePrimary = Color(0xFFB7F34A)      // Brand green
val PhBlueDark = Color(0xFF0A0A0A)         // Pure near-black header/dark background
val PhBlueLight = Color(0xFFD0FF67)        // Active green
val PhBlueContainer = Color(0xFFE8F1D3)    // Pale green selection
val PhOnBlueContainer = Color(0xFF1B2410)

val PhRedSecondary = Color(0xFFE46A4A)
val PhRedContainer = Color(0xFFFBE9E9)
val PhOnRedContainer = Color(0xFF641B1B)

val PhYellowSun = Color(0xFFFFC857)
val PhGoldContainer = Color(0xFFFFF5CF)
val PhOnGoldContainer = Color(0xFF4D3B00)

val SurfaceLight = Color(0xFFF4F0E8)       // Warm soft-white
val SurfaceVariantLight = Color(0xFFE7E1D6) // Soft neutral input/chip surface
val OnSurfaceLight = Color(0xFF171713)     // Black text
val OutlineLight = Color(0xFFC9C1B4)       // Neutral border

val SurfaceDark = Color(0xFF0B0B0A)        // BLACK
val SurfaceVariantDark = Color(0xFF151512) // Elevated black card
val OnSurfaceDark = Color(0xFFF4F0E8)      // White text
val OutlineDark = Color(0xFF34342F)        // Neutral black-mode border

val StatusOnline = Color(0xFF20D56F)
val StatusBusy = Color(0xFFD84B4B)
val StatusAway = Color(0xFFF0B83D)

// Regional accents are intentionally restrained and do not define the theme.
val RegionNcr = Color(0xFF2476A8)
val RegionVisayas = Color(0xFFB87924)
val RegionMindanao = Color(0xFF008C68)
val RegionLuzon = Color(0xFF765A91)
val RegionBicol = Color(0xFFD84B4B)

// Roles
val RoleOwner = Color(0xFFD84B4B)
val RoleAdmin = Color(0xFF00A94F)
val RoleMod = Color(0xFF008C68)
val RoleMember = Color(0xFF666666)

// Semantic aliases: UI code should describe intent, not implementation-era color names.
val PhchatAccent = PhBluePrimary
val PhchatAccentStrong = PhBlueLight
val PhchatInk = OnSurfaceLight
val PhchatCanvas = SurfaceLight
val PhchatSurface = Color.White
val PhchatGold = PhYellowSun
val PhchatDanger = PhRedSecondary
val PhchatSuccess = StatusOnline
val PhchatWarning = StatusAway
val PhchatTextPrimary = OnSurfaceLight
val PhchatTextSecondary = Color(0xFF68645C)
val PhchatTextOnAccent = Color(0xFF12120F)
val PhchatDividerLight = OutlineLight
val PhchatDividerDark = OutlineDark
