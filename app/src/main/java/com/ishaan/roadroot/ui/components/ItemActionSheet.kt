package com.ishaan.roadroot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.RoadmapItemWithProgress
import com.ishaan.roadroot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemActionSheet(
    item: RoadmapItemWithProgress,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onStatusChange: (ItemStatus) -> Unit,
    onDueDateChange: (Long?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(36.dp).height(4.dp)
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
            Text(
                text = item.item.title,
                style = MaterialTheme.typography.headlineMedium,
                color = RROnBackground,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            HorizontalDivider(color = RRBorder)
            Spacer(Modifier.height(12.dp))

            ActionRow(Icons.Outlined.DriveFileRenameOutline, "Rename") { onDismiss(); onRename() }
            ActionRow(Icons.Outlined.ContentCopy, "Duplicate") { onDuplicate(); onDismiss() }

            // Due date row
            val fmt = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            val dueDateLabel = item.item.dueDate?.let { "Due: ${fmt.format(Date(it))}" } ?: "Set due date"
            ActionRow(Icons.Outlined.CalendarMonth, dueDateLabel) { showDatePicker = true }
            if (item.item.dueDate != null) {
                ActionRow(Icons.Outlined.EventBusy, "Clear due date", tint = RROnSurfaceMuted) {
                    onDueDateChange(null); onDismiss()
                }
            }

            ActionRow(Icons.Outlined.DeleteOutline, "Delete", tint = RRError) { onDelete(); onDismiss() }

            if (item.isLeaf) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = RRBorder)
                Spacer(Modifier.height(12.dp))
                Text("STATUS", style = MaterialTheme.typography.labelMedium, color = RROnSurfaceMuted, modifier = Modifier.padding(bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip("To Do",       item.item.status == ItemStatus.TODO,        RRStatusTodo)       { onStatusChange(ItemStatus.TODO); onDismiss() }
                    StatusChip("In Progress", item.item.status == ItemStatus.IN_PROGRESS, RRStatusInProgress) { onStatusChange(ItemStatus.IN_PROGRESS); onDismiss() }
                    StatusChip("Done",        item.item.status == ItemStatus.DONE,        RRStatusDone)       { onStatusChange(ItemStatus.DONE); onDismiss() }
                    StatusChip("Discarded",   item.item.status == ItemStatus.DISCARDED,   RRStatusDiscarded)  { onStatusChange(ItemStatus.DISCARDED); onDismiss() }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = item.item.dueDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDueDateChange(it) }
                    showDatePicker = false
                    onDismiss()
                }) { Text("Set", color = RRAccent) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = RROnSurfaceMuted) }
            },
            colors = DatePickerDefaults.colors(containerColor = RRSurfaceVariant)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = RRAccent,
                    todayDateBorderColor = RRAccent,
                    containerColor = RRSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun ActionRow(icon: ImageVector, label: String, tint: androidx.compose.ui.graphics.Color = RROnSurface, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = tint)
    }
}

@Composable
private fun StatusChip(label: String, selected: Boolean, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    val bg = if (selected) color.copy(alpha = 0.18f) else RRSurfaceElevated
    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bg).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 7.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (selected) color else RROnSurfaceMuted)
    }
}
