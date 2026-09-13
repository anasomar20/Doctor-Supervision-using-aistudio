package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0ECFC),
    onPrimaryContainer = BrandPrimaryDark,
    secondary = BrandSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF3DB),
    onSecondaryContainer = Color(0xFF8C5B00),
    background = BrandBackground,
    onBackground = BrandText,
    surface = BrandSurface,
    onSurface = BrandText,
    surfaceVariant = Color(0xFFF0F4F9),
    onSurfaceVariant = BrandTextSecondary,
    outline = BrandGrayLight,
    error = BrandDanger,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8DB6F7),
    onPrimary = Color(0xFF0F2C5A),
    primaryContainer = Color(0xFF1E437C),
    onPrimaryContainer = Color(0xFFD3E4FD),
    secondary = BrandSecondary,
    onSecondary = Color.Black,
    background = Color(0xFF12161C),
    onBackground = Color(0xFFEDEFEF),
    surface = Color(0xFF1B2028),
    onSurface = Color(0xFFEDEFEF),
    surfaceVariant = Color(0xFF262E3A),
    onSurfaceVariant = Color(0xFFB0B9C6),
    outline = Color(0xFF3B4654),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
