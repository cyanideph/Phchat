package com.example.phchat.ui.theme

import androidx.compose.ui.graphics.Color

// Phchat "Violet Signal" theme.
// Dark-first: deep navy-charcoal, vivid violet, electric lime, soft mint.
// Keep color roles semantic so the palette can evolve without touching components.

// Brand primitives
val PhchatViolet = Color(0xFF7557F2)
val PhchatVioletStrong = Color(0xFF7B5CFF)
val PhchatVioletDeep = Color(0xFF6D4AE0)
val PhchatLime = Color(0xFFD4FF3F)
val PhchatLimeSoft = Color(0xFFC8F04B)
val PhchatMint = Color(0xFF5FE0D0)

// Dark surfaces
val PhchatNavy900 = Color(0xFF17152A)
val PhchatNavy950 = Color(0xFF12101F)
val PhchatNavy800 = Color(0xFF211E36)
val PhchatNavy700 = Color(0xFF2B2745)
val PhchatNavy600 = Color(0xFF383252)

// Light surfaces
val PhchatLavender50 = Color(0xFFF7F5FB)
val PhchatLavender100 = Color(0xFFEDEAF7)
val PhchatLavender200 = Color(0xFFDCD7E8)
val PhchatLavender300 = Color(0xFFB9B3C9)

// Text
val PhchatTextOnDark = Color(0xFFEDEAF7)
val PhchatTextMutedDark = Color(0xFFAAA3C2)
val PhchatTextOnLight = Color(0xFF1A1826)
val PhchatTextMutedLight = Color(0xFF625C73)

// Semantic status
val PhchatDanger = Color(0xFFFF6B6B)
val PhchatDangerContainer = Color(0xFF3A1F2A)
val PhchatSuccess = Color(0xFF5FE0D0)
val PhchatWarning = Color(0xFFFFC857)

// Compatibility aliases for existing screens.
val PhBluePrimary = PhchatLime
val PhBlueDark = PhchatNavy950
val PhBlueLight = PhchatLime
val PhBlueContainer = Color(0xFFEAF7B8)
val PhOnBlueContainer = Color(0xFF20270B)

val PhRedSecondary = PhchatDanger
val PhRedContainer = Color(0xFFFFE8E8)
val PhOnRedContainer = Color(0xFF641B1B)

val PhYellowSun = PhchatWarning
val PhGoldContainer = Color(0xFFFFF5CF)
val PhOnGoldContainer = Color(0xFF4D3B00)

val SurfaceLight = PhchatLavender50
val SurfaceVariantLight = PhchatLavender100
val OnSurfaceLight = PhchatTextOnLight
val OutlineLight = PhchatLavender300

val SurfaceDark = PhchatNavy900
val SurfaceVariantDark = PhchatNavy800
val OnSurfaceDark = PhchatTextOnDark
val OutlineDark = PhchatNavy600

val StatusOnline = PhchatMint
val StatusBusy = PhchatDanger
val StatusAway = PhchatWarning

// Regional accents stay restrained and never define the product theme.
val RegionNcr = Color(0xFF5FE0D0)
val RegionVisayas = Color(0xFFFFC857)
val RegionMindanao = Color(0xFF5FE0D0)
val RegionLuzon = PhchatViolet
val RegionBicol = PhchatDanger

// Role colors remain semantic; do not use them as decorative accents.
val RoleOwner = PhchatDanger
val RoleAdmin = PhchatMint
val RoleMod = PhchatMint
val RoleMember = PhchatTextMutedLight

// Semantic aliases used by shared components.
val PhchatAccent = PhchatLime
val PhchatAccentStrong = PhchatLimeSoft
val PhchatInk = PhchatTextOnLight
val PhchatCanvas = SurfaceLight
val PhchatSurface = Color.White
val PhchatGold = PhchatWarning
val PhchatTextPrimary = PhchatTextOnLight
val PhchatTextSecondary = PhchatTextMutedLight
val PhchatTextOnAccent = Color(0xFF17152A)
val PhchatDividerLight = PhchatLavender200
val PhchatDividerDark = PhchatNavy600
