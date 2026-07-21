package com.ishaan.roadroot.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoadmapItemCard(
    item: RoadmapItemWithProgress,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RRSurfaceVariant)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        // Left accent bar — color encodes status
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(statusColor)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.item.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = RROnBackground,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.item.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = RROnSurfaceMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (!item.isLeaf) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = RROnSurfaceMuted,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    StatusDot(status = item.item.status, color = statusColor)
                }
            }

            // Progress bar — only for parent items
            if (!item.isLeaf) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(RRSurfaceElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(animatedProgress)
                                .clip(RoundedCornerShape(2.dp))
                                .background(statusColor)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${item.progressPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${item.childCount} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = RROnSurfaceMuted
                    )
                }
            }
        }
    }
}

@Composable
fun StatusDot(status: ItemStatus, color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}
