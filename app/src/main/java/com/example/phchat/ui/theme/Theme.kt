package com.example.phchat.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PhBlueLight,
    onPrimary = Color.White,
    primaryContainer = PhBlueDark,
    onPrimaryContainer = PhBlueContainer,
    secondary = PhRedSecondary,
    onSecondary = Color.White,
    secondaryContainer = PhRedContainer,
    onSecondaryContainer = PhOnRedContainer,
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
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    outline = OutlineLight
)

@Composable
fun PhchatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use branded Philippine palette by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
