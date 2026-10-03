package com.example.phchat.ui.theme

import androidx.compose.ui.graphics.Color

// Phchat Premium Editorial palette.
// Legacy PhBlue* names remain compatibility aliases for existing screens.

val PhAcidLime = Color(0xFFD7F542)
val PhAcidLimeBright = Color(0xFFCCF52C)
val PhAcidLimePressed = Color(0xFFB7D82E)
val PhAcidLimeContainer = PhAcidLime.copy(alpha = 0.16f)
val PhBlack = Color(0xFF0A0A0C)

val PhGraphite = Color(0xFF16161A)
val PhSurface = Color(0xFF1C1C20)
val PhSurfaceRaised = Color(0xFF24242A)
val PhSurfaceSoft = Color(0xFF2B2B32)
val PhHairline = Color(0xFF34343B)

val PhLavender = Color(0xFFA78BFA)
val PhPeriwinkle = Color(0xFF8B7CF6)
val PhMint = Color(0xFF3ECF8E)
val PhTeal = Color(0xFF2FD6A3)

val PhText = Color(0xFFF2F2F5)
val PhTextMuted = Color(0xFFB0B0B8)
val PhTextSecondary = Color(0xFF8A8A93)

val PhRed = Color(0xFFFF6B6B)
val PhRedSoft = Color(0xFF3A2024)
val PhAmber = Color(0xFFF5C451)

val StatusOnline = PhMint
val StatusBusy = PhRed
val StatusAway = PhAmber

val RoleOwner = PhRed
val RoleAdmin = PhAcidLime
val RoleMod = PhLavender
val RoleMember = PhTextSecondary

val RegionNcr = PhLavender
val RegionVisayas = PhMint
val RegionMindanao = PhTeal
val RegionLuzon = PhPeriwinkle
val RegionBicol = PhRed

// Compatibility aliases.
val PhBluePrimary = PhAcidLime
val PhBlueDark = PhGraphite
val PhBlueLight = PhAcidLimeBright
val PhBlueContainer = PhAcidLime.copy(alpha = 0.16f)
val PhOnBlueContainer = PhBlack

val PhRedSecondary = PhRed
val PhRedContainer = PhRedSoft
val PhOnRedContainer = Color(0xFFFFD9DC)

val PhYellowSun = PhAcidLime
val PhGoldContainer = Color(0xFFEEF8A8)
val PhOnGoldContainer = PhBlack

val SurfaceLight = Color(0xFFF5F5F2)
val SurfaceVariantLight = Color(0xFFE8E8E4)
val OnSurfaceLight = Color(0xFF101014)
val OutlineLight = Color(0xFFD0D0CC)

val SurfaceDark = PhGraphite
val SurfaceVariantDark = PhSurface
val OnSurfaceDark = PhText
val OutlineDark = PhHairline


// Legacy component compatibility tokens.
val PhchatVioletDeep = PhPeriwinkle
val PhchatSuccess = PhMint
