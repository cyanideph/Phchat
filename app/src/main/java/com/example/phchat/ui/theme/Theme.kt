package com.example.phchat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
enum class PhchatThemeMode { System, Light, Dark }

val PhchatShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

private val DarkColorScheme = darkColorScheme(
    // Lime is reserved for action/selection. Violet owns major branded surfaces.
    primary = PhchatVioletStrong,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF34265F),
    onPrimaryContainer = Color(0xFFE7DDFF),
    secondary = PhchatMint,
    onSecondary = PhchatNavy950,
    secondaryContainer = Color(0xFF183A37),
    onSecondaryContainer = Color(0xFFB9FFF5),
    tertiary = PhchatLimeSoft,
    onTertiary = PhchatTextOnAccent,
    tertiaryContainer = Color(0xFFEAF7B8),
    onTertiaryContainer = Color(0xFF20270B),
    background = PhchatNavy950,
    onBackground = PhchatTextOnDark,
    surface = PhchatNavy900,
    onSurface = PhchatTextOnDark,
    surfaceVariant = PhchatNavy800,
    onSurfaceVariant = PhchatTextMutedDark,
    outline = PhchatNavy600,
    outlineVariant = PhchatNavy700,
    inverseSurface = PhchatLavender100,
    inverseOnSurface = PhchatTextOnLight,
    error = PhchatDanger,
    onError = Color(0xFF2B0710),
    errorContainer = PhchatDangerContainer,
    onErrorContainer = Color(0xFFFFD9DE)
)

private val LightColorScheme = lightColorScheme(
    primary = PhchatVioletStrong,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5DEFF),
    onPrimaryContainer = Color(0xFF24134F),
    secondary = PhchatMint,
    onSecondary = PhchatNavy950,
    secondaryContainer = Color(0xFFD8F8F3),
    onSecondaryContainer = Color(0xFF123A35),
    tertiary = PhchatLimeSoft,
    onTertiary = PhchatTextOnAccent,
    tertiaryContainer = Color(0xFFEAF7B8),
    onTertiaryContainer = Color(0xFF20270B),
    background = PhchatLavender50,
    onBackground = PhchatTextOnLight,
    surface = Color.White,
    onSurface = PhchatTextOnLight,
    surfaceVariant = PhchatLavender100,
    onSurfaceVariant = PhchatTextMutedLight,
    outline = PhchatLavender300,
    outlineVariant = PhchatLavender200,
    inverseSurface = PhchatNavy900,
    inverseOnSurface = PhchatTextOnDark,
    error = PhchatDanger,
    onError = Color.White,
    errorContainer = PhchatDangerContainer,
    onErrorContainer = Color(0xFFFFD9DE)
)

@Composable
fun PhchatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themeMode: PhchatThemeMode? = null,
    content: @Composable () -> Unit
) {
    // Dynamic Android colors stay disabled: the brand must remain consistent across devices.
    val resolvedDark = when (themeMode) {
        PhchatThemeMode.Dark -> true
        PhchatThemeMode.Light -> false
        PhchatThemeMode.System, null -> darkTheme
    }
    val colorScheme = if (resolvedDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = PhchatShapes,
        content = content
    )
}
