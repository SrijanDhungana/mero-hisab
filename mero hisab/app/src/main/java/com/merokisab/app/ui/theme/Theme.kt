package com.merokisab.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

val IncomeGreen = Color(0xFF4CAF50)
val ExpenseRed = Color(0xFFE53935)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    secondary = Color(0xFF1565C0),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFDFF),
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    secondary = Color(0xFF64B5F6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
)

val SerifFont = FontFamily(
    Font("font/merriweather_regular.ttf"),
    Font("font/merriweather_bold.ttf", FontWeight.Bold),
)

val SerifFontFallback = FontFamily.Default  // fallback if font not available

@Composable
fun MeroTheme(
    dark: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (dark) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}

object Typography {
    val h1 = androidx.compose.ui.text.TextStyle(
        fontFamily = SerifFontFallback,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
    )
    val h2 = androidx.compose.ui.text.TextStyle(
        fontFamily = SerifFontFallback,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    )
    val body = androidx.compose.ui.text.TextStyle(
        fontFamily = SerifFontFallback,
        fontSize = 14.sp,
    )
}