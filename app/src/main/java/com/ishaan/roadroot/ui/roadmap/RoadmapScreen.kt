package com.ishaan.roadroot.ui.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishaan.roadroot.model.RoadmapItemWithProgress
import com.ishaan.roadroot.ui.components.*
import com.ishaan.roadroot.ui.theme.*
import com.ishaan.roadroot.viewmodel.RoadmapViewModel

@Composable
fun RoadmapScreen(
    projectName: String,
    onBack: () -> Unit,
    onNavigateToItem: (projectId: Long, itemId: Long) -> Unit,
    viewModel: RoadmapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var actionItem by remember { mutableStateOf<RoadmapItemWithProgress?>(null) }
    var itemToRename by remember { mutableStateOf<RoadmapItemWithProgress?>(null) }
    var itemToDelete by remember { mutableStateOf<RoadmapItemWithProgress?>(null) }

    val screenTitle = uiState.currentItem?.title ?: projectName
    val parentName = if (uiState.currentItem == null) "Projects" else projectName

    // Description editing state
    var descriptionText by remember(uiState.currentItem?.id) {
        mutableStateOf(uiState.currentItem?.description ?: "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RRBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = RROnSurface
                    )
                }
                Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Text(
                        text = parentName,
                        style = MaterialTheme.typography.labelMedium,
                        color = RROnSurfaceMuted
                    )
                    Text(
                        text = screenTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = RROnBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Description field (for non-root item screens)
                if (uiState.currentItem != null) {
                    item {
                        DescriptionField(
                            value = descriptionText,
                            onValueChange = { descriptionText = it },
                            onFocusLost = { viewModel.updateCurrentDescription(descriptionText) }
                        )
                        Spacer(Modifier.height(16.dp))
                        if (uiState.children.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = RRBorder
                                )
                                Text(
                                    text = "  ${uiState.children.size} items  ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RROnSurfaceSubtle
                                )
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = RRBorder
                                )
                            }
                        }
                    }
                }

                if (uiState.children.isEmpty() && !uiState.isLoading) {
                    item {
                        EmptyRoadmapState(contextName = screenTitle)
                    }
                }

                items(uiState.children, key = { it.item.id }) { item ->
                    RoadmapItemCard(
                        item = item,
                        onClick = {
                            onNavigateToItem(item.item.projectId, item.item.id)
                        },
                        onLongClick = { actionItem = item }
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = RRAccent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add item")
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddItemDialog(
            contextName = screenTitle,
            onDismiss = { showAddDialog = false },
            onConfirm = { title -> viewModel.addItem(title) }
        )
    }

    actionItem?.let { item ->
        ItemActionSheet(
            item = item,
            onDismiss = { actionItem = null },
            onRename = { itemToRename = item; actionItem = null },
            onDelete = { itemToDelete = item },
            onDuplicate = { viewModel.duplicateItem(item.item) },
            onStatusChange = { status -> viewModel.updateStatus(item.item.id, status) }
        )
    }

    itemToRename?.let { item ->
        RenameDialog(
            currentName = item.item.title,
            label = "Rename item",
            onDismiss = { itemToRename = null },
            onConfirm = { newTitle -> viewModel.renameItem(item.item.id, newTitle) }
        )
    }

    itemToDelete?.let { item ->
        DeleteConfirmDialog(
            title = "Delete '${item.item.title}'?",
            message = if (item.isLeaf) "This item will be permanently deleted."
                      else "This item and all its children will be permanently deleted.",
            onDismiss = { itemToDelete = null; actionItem = null },
            onConfirm = { viewModel.deleteItem(item.item.id) }
        )
    }
}

@Composable
private fun DescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (focused) RRSurfaceVariant else RRBackground)
            .padding(if (focused) 12.dp else 0.dp)
    ) {
        if (value.isEmpty() && !focused) {
            Text(
                text = "Add a description…",
                style = MaterialTheme.typography.bodyMedium,
                color = RROnSurfaceSubtle,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    if (focused && !state.isFocused) onFocusLost()
                    focused = state.isFocused
                },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = RROnSurface),
            cursorBrush = SolidColor(RRAccent),
            onTextLayout = {}
        )
    }
}

@Composable
private fun EmptyRoadmapState(contextName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nothing here yet",
            style = MaterialTheme.typography.headlineMedium,
            color = RROnSurfaceMuted
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tap + to add items to $contextName",
            style = MaterialTheme.typography.bodyMedium,
            color = RROnSurfaceSubtle
        )
    }
}
