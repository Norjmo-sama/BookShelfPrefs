package com.example.bookshelfprefs.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF8A4B2A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCB),
    onPrimaryContainer = Color(0xFF341100),
    secondaryContainer = Color(0xFFF2DFD5),
    background = Color(0xFFFFF8F5),
    surface = Color(0xFFFFF8F5),
    surfaceVariant = Color(0xFFF4DED4),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB691),
    onPrimary = Color(0xFF532200),
    primaryContainer = Color(0xFF6E3515),
    onPrimaryContainer = Color(0xFFFFDBCB),
    secondaryContainer = Color(0xFF5C4033),
    background = Color(0xFF1A110D),
    surface = Color(0xFF1A110D),
    surfaceVariant = Color(0xFF52443C),
)

/** darkTheme comes from the SAVED preference, not the phone's system setting. */
@Composable
fun BookShelfTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}