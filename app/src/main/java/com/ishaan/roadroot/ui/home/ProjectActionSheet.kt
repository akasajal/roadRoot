package com.ishaan.roadroot.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.ProjectAccent
import com.ishaan.roadroot.ui.components.ColorPickerRow
import com.ishaan.roadroot.ui.components.RenameDialog
import com.ishaan.roadroot.ui.theme.*
import com.ishaan.roadroot.viewmodel.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectActionSheet(
    project: Project,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    viewModel: ProjectViewModel = hiltViewModel()
) {
    var showRename by remember { mutableStateOf(false) }
    var selectedAccent by remember {
        mutableStateOf(ProjectAccent.fromArgb(project.accentColor))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(RROnSurfaceSubtle)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.headlineMedium,
                color = RROnBackground,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            HorizontalDivider(color = RRBorder)
            Spacer(Modifier.height(12.dp))

            // Color picker
            Text(
                text = "COLOR",
                style = MaterialTheme.typography.labelMedium,
                color = RROnSurfaceMuted,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            ColorPickerRow(
                selected = selectedAccent,
                onSelect = { accent ->
                    selectedAccent = accent
                    viewModel.updateAccentColor(project, accent)
                }
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = RRBorder)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showRename = true }
                    .padding(vertical = 14.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.DriveFileRenameOutline, contentDescription = "Rename", tint = RROnSurface, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(14.dp))
                Text("Rename", style = MaterialTheme.typography.bodyMedium, color = RROnSurface)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDelete(); onDismiss() }
                    .padding(vertical = 14.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete", tint = RRError, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(14.dp))
                Text("Delete", style = MaterialTheme.typography.bodyMedium, color = RRError)
            }
        }
    }

    if (showRename) {
        RenameDialog(
            currentName = project.name,
            label = "Rename project",
            onDismiss = { showRename = false; onDismiss() },
            onConfirm = { newName ->
                viewModel.renameProject(project, newName)
                onDismiss()
            }
        )
    }
}
