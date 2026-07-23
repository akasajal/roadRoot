package com.ishaan.roadroot.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val surfaceElevated: Color,
    val onSurfaceSubtle: Color,
    val statusDone: Color,
    val statusInProgress: Color,
    val statusTodo: Color,
    val statusDiscarded: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        surfaceElevated = Color.Unspecified,
        onSurfaceSubtle = Color.Unspecified,
        statusDone = Color.Unspecified,
        statusInProgress = Color.Unspecified,
        statusTodo = Color.Unspecified,
        statusDiscarded = Color.Unspecified
    )
}

private val DarkRoadRootColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF052E16),
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = Color(0xFFBBF7D0),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF1E293B),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFCBD5E1),
    background = Color(0xFF0E1117),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF161B26),
    onSurface = Color(0xFFCBD5E1),
    surfaceVariant = Color(0xFF1E2535),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFF1E293B),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
)

private val DarkExtendedColors = ExtendedColors(
    surfaceElevated = Color(0xFF242C3D),
    onSurfaceSubtle = Color(0xFF334155),
    statusDone = Color(0xFF4ADE80),
    statusInProgress = Color(0xFFFBBF24),
    statusTodo = Color(0xFF475569),
    statusDiscarded = Color(0xFFCB4C4E)
)

private val LightRoadRootColorScheme = lightColorScheme(
    primary = Color(0xFF22C55E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF14532D),
    secondary = Color(0xFF475569),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1F5F9),
    onSecondaryContainer = Color(0xFF1E293B),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0),
    error = Color(0xFFEF4444),
    onError = Color.White,
)

private val LightExtendedColors = ExtendedColors(
    surfaceElevated = Color(0xFFF1F5F9),
    onSurfaceSubtle = Color(0xFF94A3B8),
    statusDone = Color(0xFF22C55E),
    statusInProgress = Color(0xFFD97706),
    statusTodo = Color(0xFF64748B),
    statusDiscarded = Color(0xFFDC2626)
)

@Composable
fun RoadRootTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkRoadRootColorScheme else LightRoadRootColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = RoadRootTypography,
            content = content
        )
    }
}
