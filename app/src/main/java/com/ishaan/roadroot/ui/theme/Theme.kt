package com.ishaan.roadroot.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RoadRootColorScheme = darkColorScheme(
    primary = RRAccent,
    onPrimary = Color(0xFF052E16),
    primaryContainer = RRAccentContainer,
    onPrimaryContainer = Color(0xFFBBF7D0),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF1E293B),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFCBD5E1),
    background = RRBackground,
    onBackground = RROnBackground,
    surface = RRSurface,
    onSurface = RROnSurface,
    surfaceVariant = RRSurfaceVariant,
    onSurfaceVariant = RROnSurfaceMuted,
    outline = RRBorder,
    error = RRError,
    onError = Color(0xFF450A0A),
)

@Composable
fun RoadRootTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RoadRootColorScheme,
        typography = RoadRootTypography,
        content = content
    )
}
