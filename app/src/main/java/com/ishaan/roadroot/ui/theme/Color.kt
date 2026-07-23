package com.ishaan.roadroot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// RoadRoot palette — using MaterialTheme where possible for dynamic colors
val RRBackground: Color @Composable get() = MaterialTheme.colorScheme.background
val RRSurface: Color @Composable get() = MaterialTheme.colorScheme.surface
val RRSurfaceVariant: Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val RRSurfaceElevated: Color @Composable get() = LocalExtendedColors.current.surfaceElevated

val RRAccent: Color @Composable get() = MaterialTheme.colorScheme.primary
val RRAccentContainer: Color @Composable get() = MaterialTheme.colorScheme.primaryContainer

val RROnBackground: Color @Composable get() = MaterialTheme.colorScheme.onBackground
val RROnSurface: Color @Composable get() = MaterialTheme.colorScheme.onSurface
val RROnSurfaceMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val RROnSurfaceSubtle: Color @Composable get() = LocalExtendedColors.current.onSurfaceSubtle

val RRStatusDone: Color @Composable get() = LocalExtendedColors.current.statusDone
val RRStatusInProgress: Color @Composable get() = LocalExtendedColors.current.statusInProgress
val RRStatusTodo: Color @Composable get() = LocalExtendedColors.current.statusTodo
val RRStatusDiscarded: Color @Composable get() = LocalExtendedColors.current.statusDiscarded

val RRBorder: Color @Composable get() = MaterialTheme.colorScheme.outline
val RRError: Color @Composable get() = MaterialTheme.colorScheme.error

// Static accent colors for project items (used in ProjectAccent enum)
val RRProjectGreen = Color(0xFF4ADE80)
val RRProjectBlue = Color(0xFF60A5FA)
val RRProjectPurple = Color(0xFFA78BFA)
val RRProjectOrange = Color(0xFFFBBF24)
val RRProjectRose = Color(0xFFFB7185)
val RRProjectSlate = Color(0xFF94A3B8)
