package com.ishaan.roadroot.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.RoadmapItemWithProgress
import com.ishaan.roadroot.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoadmapItemCard(
    item: RoadmapItemWithProgress,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    childTitles: List<String> = emptyList(),
    // Caller passes Modifier.draggableHandle(...) from reorderable; null = no handle shown
    dragModifier: Modifier? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = item.progress,
        animationSpec = tween(600),
        label = "progress"
    )

    val statusColor = when (item.effectiveStatus) {
        ItemStatus.DONE -> RRStatusDone
        ItemStatus.IN_PROGRESS -> RRStatusInProgress
        ItemStatus.TODO -> RRStatusTodo
    }

    val now = System.currentTimeMillis()
    val isOverdue = item.item.dueDate != null &&
            item.item.dueDate < now &&
            item.effectiveStatus != ItemStatus.DONE

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RRSurfaceVariant)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (isOverdue) RRError else statusColor)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle — only rendered when dragModifier is provided
            if (dragModifier != null) {
                Icon(
                    imageVector = Icons.Outlined.DragHandle,
                    contentDescription = "Drag to reorder",
                    tint = RROnSurfaceSubtle,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(20.dp)
                        .then(dragModifier)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (dragModifier != null) 8.dp else 16.dp,
                        end = 12.dp,
                        top = 14.dp,
                        bottom = 14.dp
                    )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.item.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = RROnBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.item.description.isNotBlank()) {
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = item.item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = RROnSurfaceMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    if (!item.isLeaf) {
                        Icon(Icons.Default.ChevronRight, "Navigate", tint = RROnSurfaceMuted, modifier = Modifier.size(18.dp))
                    } else {
                        StatusDot(status = item.item.status, color = statusColor)
                    }
                }

                // Visual tree preview
                if (!item.isLeaf && childTitles.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        childTitles.take(4).forEach { title ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("├─", style = MaterialTheme.typography.labelSmall, color = RROnSurfaceSubtle, modifier = Modifier.padding(end = 6.dp))
                                Text(title, style = MaterialTheme.typography.labelSmall, color = RROnSurfaceMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        if (childTitles.size > 4) {
                            Text("  +${childTitles.size - 4} more", style = MaterialTheme.typography.labelSmall, color = RROnSurfaceSubtle)
                        }
                    }
                }

                // Progress bar
                if (!item.isLeaf) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.weight(1f).height(3.dp)
                                .clip(RoundedCornerShape(2.dp)).background(RRSurfaceElevated)
                        ) {
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(animatedProgress).clip(RoundedCornerShape(2.dp)).background(statusColor))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("${item.progressPercent}%", style = MaterialTheme.typography.labelSmall, color = statusColor)
                        Spacer(Modifier.width(6.dp))
                        Text("${item.childCount} items", style = MaterialTheme.typography.labelSmall, color = RROnSurfaceMuted)
                    }
                }

                // Due date
                item.item.dueDate?.let { due ->
                    Spacer(Modifier.height(8.dp))
                    val fmt = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                    Text(
                        text = "Due ${fmt.format(Date(due))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOverdue) RRError else RROnSurfaceMuted
                    )
                }
            }
        }
    }
}

@Composable
fun StatusDot(status: ItemStatus, color: Color) {
    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(color))
}
