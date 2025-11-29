package com.example.myapplication

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val NothingRed = Color(0xFFD71921)
val DarkGray = Color(0xFF121212)
val LightGray = Color(0xFF202020)

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black,
    primaryContainer = LightGray,
    onPrimaryContainer = White,
    secondary = NothingRed,
    onSecondary = White,
    secondaryContainer = NothingRed,
    onSecondaryContainer = White,
    tertiary = White,
    onTertiary = Black,
    background = Black,
    onBackground = White,
    surface = DarkGray,
    onSurface = White,
    surfaceVariant = LightGray,
    onSurfaceVariant = White,
    outline = White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
