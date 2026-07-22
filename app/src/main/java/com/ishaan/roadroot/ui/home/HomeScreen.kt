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
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Search
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
import com.ishaan.roadroot.ui.components.*
import com.ishaan.roadroot.ui.theme.*
import com.ishaan.roadroot.viewmodel.ProjectViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onOpenProject: (Long) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenStats: () -> Unit,
    viewModel: ProjectViewModel = hiltViewModel()
) {
    val projects by viewModel.projects.collectAsState()
    val importResult by viewModel.importResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showTemplateSheet by remember { mutableStateOf(false) }
    var templatePicked by remember { mutableStateOf(false) }
    var pendingProjectName by remember { mutableStateOf("") }
    var pendingAccent by remember { mutableStateOf(ProjectAccent.GREEN) }
    var projectToAction by remember { mutableStateOf<Project?>(null) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    // Show import result as Snackbar
    LaunchedEffect(importResult) {
        if (importResult != null) {
            scope.launch { snackbarHostState.showSnackbar(importResult!!) }
            viewModel.clearImportResult()
        }
    }

    Scaffold(
        containerColor = RRBackground,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = RRAccent,
                contentColor = Color(0xFF052E16),
                shape = CircleShape,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add project")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(RRBackground)
        ) {
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                // Header row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 8.dp,
                            bottom = 20.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RoadRoot", style = MaterialTheme.typography.headlineLarge, color = RROnBackground)
                        Text("Your projects", style = MaterialTheme.typography.bodyMedium, color = RROnSurfaceMuted)
                    }
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Outlined.Search, "Search", tint = RROnSurfaceMuted)
                    }
                    IconButton(onClick = onOpenStats) {
                        Icon(Icons.Outlined.BarChart, "Stats", tint = RROnSurfaceMuted)
                    }
                }

                if (projects.isEmpty()) {
                    EmptyState(modifier = Modifier.weight(1f).fillMaxWidth())
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 100.dp),
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
        }
    }

    // Create project dialog → then template sheet
    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, accent ->
                pendingProjectName = name
                pendingAccent = accent
                showCreateDialog = false
                showTemplateSheet = true
            },
            onImportJson = { uri ->
                viewModel.importFromJson(uri)
            }
        )
    }

    if (showTemplateSheet) {
        TemplateSheet(
            onDismiss = {
                showTemplateSheet = false
                // Only create a blank project if user dismissed without picking a template
                if (!templatePicked) {
                    viewModel.createProject(pendingProjectName, pendingAccent.argb)
                }
                templatePicked = false
            },
            onSelectTemplate = { template ->
                templatePicked = true
                showTemplateSheet = false
                viewModel.createProjectFromTemplate(pendingProjectName, pendingAccent.argb, template.items)
            }
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
private fun ProjectCard(project: Project, accent: ProjectAccent, onClick: () -> Unit, onLongClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(RRSurfaceVariant)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(accent.color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.AccountTree, null, tint = accent.color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, style = MaterialTheme.typography.titleLarge, color = RROnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (project.description.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(project.description, style = MaterialTheme.typography.bodySmall, color = RROnSurfaceMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent.color))
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Outlined.AccountTree, null, tint = RROnSurfaceSubtle, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("No projects yet", style = MaterialTheme.typography.headlineMedium, color = RROnSurfaceMuted)
        Spacer(Modifier.height(6.dp))
        Text("Tap + to start your first roadmap", style = MaterialTheme.typography.bodyMedium, color = RROnSurfaceSubtle)
    }
}
