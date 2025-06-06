package com.example.museart.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Определение цветов
val Blue = Color(0xFF1DA1F2)
val BlueLight = Color(0xFF60C5FF)
val BlueDark = Color(0xFF0078C1)

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)

val Gray50 = Color(0xFFF9FAFB)
val Gray100 = Color(0xFFF3F4F6)
val Gray200 = Color(0xFFE5E7EB)
val Gray300 = Color(0xFFD1D5DB)
val Gray400 = Color(0xFF9CA3AF)
val Gray500 = Color(0xFF6B7280)
val Gray600 = Color(0xFF4B5563)
val Gray700 = Color(0xFF374151)
val Gray800 = Color(0xFF1F2937)
val Gray900 = Color(0xFF111827)

// Светлая тема
private val LightColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = White,
    secondary = BlueLight,
    onSecondary = Black,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
)

// Темная тема
private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    onPrimary = Black,
    secondary = BlueLight,
    onSecondary = Black,
    background = Black,
    onBackground = White,
    surface = Gray900,
    onSurface = White,
)

@Composable
fun MuseArtTheme(
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

