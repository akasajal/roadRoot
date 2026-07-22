package com.ishaan.roadroot.ui.export

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.ui.theme.*
import com.ishaan.roadroot.viewmodel.ExportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSheet(
    project: Project,
    onDismiss: () -> Unit,
    viewModel: ExportViewModel
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp).width(36.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(RROnSurfaceSubtle))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 36.dp)) {
            Text("Export \"${project.name}\"", style = MaterialTheme.typography.headlineMedium, color = RROnBackground, modifier = Modifier.padding(bottom = 16.dp))
            HorizontalDivider(color = RRBorder)
            Spacer(Modifier.height(8.dp))
            ExportRow(Icons.Outlined.Description, "Markdown (.md)", "Readable outline with status") {
                viewModel.exportMarkdown(project); onDismiss()
            }
            ExportRow(Icons.Outlined.DataObject, "JSON (.json)", "Structured data with full hierarchy") {
                viewModel.exportJson(project); onDismiss()
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = RRBorder)
            Spacer(Modifier.height(8.dp))
            ExportRow(Icons.Outlined.CloudDownload, "Backup all projects", "JSON backup of everything") {
                viewModel.backupAll(); onDismiss()
            }
        }
    }
}

@Composable
private fun ExportRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = RRAccent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = RROnSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = RROnSurfaceMuted)
        }
    }
}
