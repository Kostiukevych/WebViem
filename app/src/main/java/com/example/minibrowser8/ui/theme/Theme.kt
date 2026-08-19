package com.example.minibrowser8.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0284C7),
    secondary = Color(0xFF38BDF8),
    tertiary = Color(0xFF059669),
    background = Color(0xFF090D16),
    surface = Color(0xFF131B2E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
)

@Composable
fun MiniBrowserTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
