package com.ishaan.roadroot.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishaan.roadroot.model.AppTheme
import com.ishaan.roadroot.ui.theme.RRBackground
import com.ishaan.roadroot.ui.theme.RROnBackground
import com.ishaan.roadroot.ui.theme.RROnSurfaceMuted
import com.ishaan.roadroot.ui.theme.RRSurfaceVariant
import com.ishaan.roadroot.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val theme by viewModel.theme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = RROnBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = RROnBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RRBackground
                )
            )
        },
        containerColor = RRBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(RRBackground),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SectionHeader("Appearance")
            }
            
            item {
                ThemeOption(
                    title = "System Default",
                    selected = theme == AppTheme.SYSTEM,
                    onClick = { viewModel.setTheme(AppTheme.SYSTEM) }
                )
            }
            item {
                ThemeOption(
                    title = "Light",
                    selected = theme == AppTheme.LIGHT,
                    onClick = { viewModel.setTheme(AppTheme.LIGHT) }
                )
            }
            item {
                ThemeOption(
                    title = "Dark",
                    selected = theme == AppTheme.DARK,
                    onClick = { viewModel.setTheme(AppTheme.DARK) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = RROnSurfaceMuted,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = RROnBackground,
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = null, // Handled by row click
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = RROnSurfaceMuted
            )
        )
    }
}
