package com.example.phchat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PhAcidLime,
    onPrimary = PhBlack,
    primaryContainer = PhAcidLime.copy(alpha = 0.18f),
    onPrimaryContainer = PhAcidLime,
    secondary = PhLavender,
    onSecondary = PhBlack,
    secondaryContainer = PhLavender.copy(alpha = 0.18f),
    onSecondaryContainer = PhText,
    tertiary = PhMint,
    onTertiary = PhBlack,
    tertiaryContainer = PhMint.copy(alpha = 0.18f),
    onTertiaryContainer = PhText,
    background = PhGraphite,
    onBackground = PhText,
    surface = PhSurface,
    onSurface = PhText,
    surfaceVariant = PhSurfaceRaised,
    onSurfaceVariant = PhTextSecondary,
    outline = PhHairline,
    error = PhRed,
    onError = PhBlack,
    errorContainer = PhRedSoft,
    onErrorContainer = Color(0xFFFFD9DC)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF657A00),
    onPrimary = PhBlack,
    primaryContainer = Color(0xFFE8F39D),
    onPrimaryContainer = PhBlack,
    secondary = Color(0xFF6548C5),
    onSecondary = PhBlack,
    secondaryContainer = Color(0xFFE9E2FF),
    onSecondaryContainer = Color(0xFF24194B),
    tertiary = Color(0xFF14784F),
    onTertiary = PhBlack,
    tertiaryContainer = Color(0xFFD9F7E8),
    onTertiaryContainer = Color(0xFF063B24),
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = Color.White,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF5F6068),
    outline = OutlineLight,
    error = Color(0xFFB4232E),
    onError = Color.White,
    errorContainer = Color(0xFFFFE2E4),
    onErrorContainer = Color(0xFF5F1017)
)

@Composable
fun PhchatTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
