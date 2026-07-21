package com.ishaan.roadroot.ui.components

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
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.RoadmapItemWithProgress
import com.ishaan.roadroot.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemActionSheet(
    item: RoadmapItemWithProgress,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onStatusChange: (ItemStatus) -> Unit
) {
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
                .padding(bottom = 32.dp)
        ) {
            // Item label
            Text(
                text = item.item.title,
                style = MaterialTheme.typography.headlineMedium,
                color = RROnBackground,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            HorizontalDivider(color = RRBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            ActionRow(
                icon = Icons.Outlined.DriveFileRenameOutline,
                label = "Rename",
                onClick = { onDismiss(); onRename() }
            )
            ActionRow(
                icon = Icons.Outlined.ContentCopy,
                label = "Duplicate",
                onClick = { onDuplicate(); onDismiss() }
            )
            ActionRow(
                icon = Icons.Outlined.DeleteOutline,
                label = "Delete",
                tint = RRError,
                onClick = { onDelete(); onDismiss() }
            )

            // Status section — only for leaf items
            if (item.isLeaf) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = RRBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "STATUS",
                    style = MaterialTheme.typography.labelMedium,
                    color = RROnSurfaceMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(
                        label = "To Do",
                        selected = item.item.status == ItemStatus.TODO,
                        color = RRStatusTodo,
                        onClick = { onStatusChange(ItemStatus.TODO); onDismiss() }
                    )
                    StatusChip(
                        label = "In Progress",
                        selected = item.item.status == ItemStatus.IN_PROGRESS,
                        color = RRStatusInProgress,
                        onClick = { onStatusChange(ItemStatus.IN_PROGRESS); onDismiss() }
                    )
                    StatusChip(
                        label = "Done",
                        selected = item.item.status == ItemStatus.DONE,
                        color = RRStatusDone,
                        onClick = { onStatusChange(ItemStatus.DONE); onDismiss() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color = RROnSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = tint
        )
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val bg = if (selected) color.copy(alpha = 0.18f) else RRSurfaceElevated
    val textColor = if (selected) color else RROnSurfaceMuted

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}
