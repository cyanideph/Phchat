package com.example.phchat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PhBlueLight,
    onPrimary = Color(0xFF00210E),
    primaryContainer = Color(0xFF35451D),
    onPrimaryContainer = Color(0xFFE8F7C6),
    secondary = Color(0xFFB8B2A5),
    onSecondary = Color(0xFF191814),
    secondaryContainer = Color(0xFF2A2A24),
    onSecondaryContainer = Color(0xFFE3DED3),
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB7B1A6),
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PhBluePrimary,
    onPrimary = Color(0xFF12120F),
    primaryContainer = PhBlueContainer,
    onPrimaryContainer = PhOnBlueContainer,
    secondary = Color(0xFF5E6652),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4E0D6),
    onSecondaryContainer = Color(0xFF292820),
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = Color.White,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF68645C),
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
