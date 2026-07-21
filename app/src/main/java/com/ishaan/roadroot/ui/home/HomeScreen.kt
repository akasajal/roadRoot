package com.ishaan.roadroot.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.ProjectAccent
import com.ishaan.roadroot.ui.components.CreateProjectDialog
import com.ishaan.roadroot.ui.components.DeleteConfirmDialog
import com.ishaan.roadroot.ui.theme.*
import com.ishaan.roadroot.viewmodel.ProjectViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onOpenProject: (Long) -> Unit,
    viewModel: ProjectViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var projectToAction by remember { mutableStateOf<Project?>(null) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RRBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        text = "RoadRoot",
                        style = MaterialTheme.typography.headlineLarge,
                        color = RROnBackground
                    )
                    Text(
                        text = "Your projects",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RROnSurfaceMuted
                    )
                }
            }

            if (projects.isEmpty()) {
                EmptyState(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(projects, key = { it.id }) { project ->
                        val accent = ProjectAccent.fromArgb(project.accentColor)
                        ProjectCard(
                            project = project,
                            accent = accent,
                            onClick = { onOpenProject(project.id) },
                            onLongClick = { projectToAction = project }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = RRAccent,
            contentColor = Color(0xFF052E16),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add project")
        }
    }

    if (showAddDialog) {
        CreateProjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, accent -> viewModel.createProject(name, accent.argb) }
        )
    }

    projectToAction?.let { project ->
        ProjectActionSheet(
            project = project,
            onDismiss = { projectToAction = null },
            onRename = {},
            onDelete = { projectToDelete = project; projectToAction = null }
        )
    }

    projectToDelete?.let { project ->
        DeleteConfirmDialog(
            title = "Delete '${project.name}'?",
            message = "This will permanently delete the project and all its roadmap items.",
            onDismiss = { projectToDelete = null },
            onConfirm = { viewModel.deleteProject(project) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProjectCard(
    project: Project,
    accent: ProjectAccent,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(RRSurfaceVariant)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountTree,
                    contentDescription = null,
                    tint = accent.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = RROnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (project.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = RROnSurfaceMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // Accent dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent.color)
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountTree,
            contentDescription = null,
            tint = RROnSurfaceSubtle,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No projects yet", style = MaterialTheme.typography.headlineMedium, color = RROnSurfaceMuted)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Tap + to start your first roadmap", style = MaterialTheme.typography.bodyMedium, color = RROnSurfaceSubtle)
    }
}
