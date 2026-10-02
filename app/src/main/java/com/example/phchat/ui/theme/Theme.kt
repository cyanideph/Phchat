package com.example.phchat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF78C7AE),
    onPrimary = Color(0xFF07352B),
    primaryContainer = Color(0xFF174D40),
    onPrimaryContainer = Color(0xFFC6EBDD),
    secondary = Color(0xFFE78C87),
    onSecondary = Color(0xFF4A1010),
    secondaryContainer = Color(0xFF642522),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PhBluePrimary,
    onPrimary = Color.White,
    primaryContainer = PhBlueContainer,
    onPrimaryContainer = PhOnBlueContainer,
    secondary = PhRedSecondary,
    onSecondary = Color.White,
    secondaryContainer = PhRedContainer,
    onSecondaryContainer = PhOnRedContainer,
    tertiary = PhYellowSun,
    onTertiary = PhOnGoldContainer,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = Color(0xFFF9FCFB),
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF5D716A),
    outline = OutlineLight
)

@Composable
fun PhchatTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // PHchat deliberately does not use Android dynamic colors so the brand palette
    // remains consistent across devices.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
