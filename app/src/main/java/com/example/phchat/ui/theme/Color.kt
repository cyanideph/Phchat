package com.example.phchat.ui.theme

import androidx.compose.ui.graphics.Color

// Phchat "Acid Signal": premium graphite-charcoal, acid lime, lavender and mint.
// Lime is the hero action/selection color; lavender and mint are supporting surfaces.

val PhchatLime = Color(0xFFD7F542)
val PhchatLimeSoft = Color(0xFFCCF52C)
val PhchatViolet = Color(0xFFA78BFA)
val PhchatVioletStrong = Color(0xFF8B7CF6)
val PhchatVioletDeep = Color(0xFF7C6AE8)
val PhchatMint = Color(0xFF3ECF8E)
val PhchatMintStrong = Color(0xFF2FD6A3)

val PhchatNavy950 = Color(0xFF16161A)
val PhchatNavy900 = Color(0xFF1C1C20)
val PhchatNavy800 = Color(0xFF242429)
val PhchatNavy700 = Color(0xFF2D2D33)
val PhchatNavy600 = Color(0xFF3A3A42)

val PhchatLavender50 = Color(0xFFF7F6FA)
val PhchatLavender100 = Color(0xFFEDEAF8)
val PhchatLavender200 = Color(0xFFD8D2EB)
val PhchatLavender300 = Color(0xFFAFA9BD)

val PhchatTextOnDark = Color(0xFFF2F2F5)
val PhchatTextMutedDark = Color(0xFF8A8A93)
val PhchatTextOnLight = Color(0xFF0A0A0C)
val PhchatTextMutedLight = Color(0xFF5F5F67)

val PhchatDanger = Color(0xFFFF6B6B)
val PhchatDangerContainer = Color(0xFF3A1F2A)
val PhchatSuccess = PhchatMintStrong
val PhchatWarning = Color(0xFFFFC857)

// Legacy compatibility aliases: blue-era names now map to the new visual roles.
val PhBluePrimary = PhchatVioletDeep
val PhBlueDark = PhchatNavy950
val PhBlueLight = PhchatLime
val PhBlueContainer = Color(0xFFEDE8FF)
val PhOnBlueContainer = Color(0xFF0A0A0C)

val PhRedSecondary = PhchatDanger
val PhRedContainer = Color(0xFFFFE8E8)
val PhOnRedContainer = Color(0xFF641B1B)

val PhYellowSun = PhchatWarning
val PhGoldContainer = Color(0xFFFFF5CF)
val PhOnGoldContainer = Color(0xFF4D3B00)

val SurfaceLight = Color(0xFFF4F2F7)
val SurfaceVariantLight = Color(0xFFE9E6EE)
val OnSurfaceLight = PhchatTextOnLight
val OutlineLight = Color(0xFFA7A3AE)

val SurfaceDark = PhchatNavy900
val SurfaceVariantDark = PhchatNavy800
val OnSurfaceDark = PhchatTextOnDark
val OutlineDark = PhchatNavy600

val StatusOnline = PhchatMint
val StatusBusy = PhchatDanger
val StatusAway = PhchatWarning

// Regional accents remain restrained and subordinate to the product palette.
val RegionNcr = PhchatMint
val RegionVisayas = PhchatViolet
val RegionMindanao = PhchatMint
val RegionLuzon = PhchatViolet
val RegionBicol = PhchatDanger

val RoleOwner = PhchatDanger
val RoleAdmin = PhchatMint
val RoleMod = PhchatMint
val RoleMember = PhchatTextMutedLight

val PhchatAccent = PhchatLime
val PhchatAccentStrong = PhchatLimeSoft
val PhchatInk = PhchatTextOnLight
val PhchatCanvas = SurfaceLight
val PhchatSurface = Color.White
val PhchatGold = PhchatWarning
val PhchatTextPrimary = PhchatTextOnLight
val PhchatTextSecondary = PhchatTextMutedLight
val PhchatTextOnAccent = Color(0xFF0A0A0C)
val PhchatDividerLight = PhchatLavender200
val PhchatDividerDark = PhchatNavy600
