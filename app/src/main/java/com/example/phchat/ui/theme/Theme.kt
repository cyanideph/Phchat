package com.example.phchat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PhBlueLight,
    onPrimary = Color(0xFF00210E),
    primaryContainer = Color(0xFF006B35),
    onPrimaryContainer = Color(0xFFB5FFD0),
    secondary = Color(0xFF78DFA4),
    onSecondary = Color(0xFF00391B),
    secondaryContainer = Color(0xFF0E4328),
    onSecondaryContainer = Color(0xFFBFFFCF),
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB8B8B3),
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PhBluePrimary,
    onPrimary = Color.White,
    primaryContainer = PhBlueContainer,
    onPrimaryContainer = PhOnBlueContainer,
    secondary = Color(0xFF16834A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1F5E9),
    onSecondaryContainer = Color(0xFF0A3B21),
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = Color.White,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF60757E),
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
    // Dynamic Android colors stay disabled so the Soft Pro palette remains identical
    // across phones and does not get replaced by OEM wallpaper colors.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
