package com.example.phchat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PhAcidLime, onPrimary = PhBlack,
    primaryContainer = PhAcidLime.copy(alpha = 0.18f), onPrimaryContainer = PhAcidLime,
    secondary = PhLavender, onSecondary = PhBlack,
    secondaryContainer = PhLavender.copy(alpha = 0.18f), onSecondaryContainer = PhText,
    tertiary = PhMint, onTertiary = PhBlack,
    tertiaryContainer = PhMint.copy(alpha = 0.18f), onTertiaryContainer = PhText,
    background = PhGraphite, onBackground = PhText,
    surface = PhSurface, onSurface = PhText,
    surfaceVariant = PhSurfaceRaised, onSurfaceVariant = PhTextSecondary,
    outline = PhHairline, error = PhRed, onError = PhBlack,
    errorContainer = PhRedSoft, onErrorContainer = PhText
)

private val LightColorScheme = lightColorScheme(
    primary = PhLightPrimary, onPrimary = PhBlack,
    primaryContainer = PhLightPrimaryContainer, onPrimaryContainer = PhBlack,
    secondary = PhLightSecondary, onSecondary = PhBlack,
    secondaryContainer = PhLightSecondaryContainer, onSecondaryContainer = PhTextSecondary,
    tertiary = PhLightTertiary, onTertiary = PhBlack,
    tertiaryContainer = PhLightTertiaryContainer, onTertiaryContainer = Color(0xFF063B24),
    background = PhLightBackground, onBackground = PhLightText,
    surface = PhLightSurface, onSurface = PhLightText,
    surfaceVariant = PhLightSurfaceVariant, onSurfaceVariant = PhLightTextSecondary,
    outline = PhLightOutline, error = PhLightError, onError = Color.White,
    errorContainer = PhLightErrorContainer, onErrorContainer = Color(0xFF5F1017)
)

@Composable
fun PhchatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = PhchatShapes.material(),
        content = content
    )
}