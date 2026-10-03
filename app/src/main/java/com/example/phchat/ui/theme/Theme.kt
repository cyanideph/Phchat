package com.example.phchat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PhYellowSun,
    onPrimary = Color(0xFF241500),
    primaryContainer = Color(0xFF3A2808),
    onPrimaryContainer = Color(0xFFFFE3A7),
    secondary = Color(0xFFE2E2E0),
    onSecondary = Color(0xFF17171A),
    secondaryContainer = Color(0xFF2A2A2D),
    onSecondaryContainer = Color(0xFFE9E9E7),
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB5B5B2),
    outline = OutlineDark,
    error = PhRedSecondary,
    onError = Color.White,
    errorContainer = Color(0xFF3A1717),
    onErrorContainer = Color(0xFFFFDAD7)
)

private val LightColorScheme = lightColorScheme(
    primary = PhBluePrimary,
    onPrimary = Color.White,
    primaryContainer = PhBlueContainer,
    onPrimaryContainer = PhOnBlueContainer,
    secondary = Color(0xFF5D5D63),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9E9E7),
    onSecondaryContainer = Color(0xFF1D1D21),
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = Color.White,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF66666C),
    outline = OutlineLight,
    error = PhRedSecondary,
    onError = Color.White,
    errorContainer = PhRedContainer,
    onErrorContainer = PhOnRedContainer
)

@Composable
fun PhchatTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Dynamic colors stay disabled so OEM wallpaper palettes cannot change Phchat's identity.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
