package com.ishaan.roadroot.ui.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishaan.roadroot.model.ProjectAccent
import com.ishaan.roadroot.ui.theme.RRBackground
import com.ishaan.roadroot.ui.theme.RROnBackground
import com.ishaan.roadroot.ui.theme.RROnSurfaceSubtle
import com.ishaan.roadroot.viewmodel.GraphViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphViewScreen(
    onBack: () -> Unit,
    viewModel: GraphViewModel = hiltViewModel()
) {
    val project by viewModel.project.collectAsState()
    val nodes = viewModel.nodes
    val edges = viewModel.edges
    val highlightedNodeId by viewModel.highlightedNodeId.collectAsState()
    val highlightColor by viewModel.highlightColor.collectAsState()

    val accent = project?.let { ProjectAccent.fromArgb(it.accentColor) } ?: ProjectAccent.GREEN
    val edgeColor = RROnSurfaceSubtle.copy(alpha = 0.2f)
    val labelColor = RROnBackground

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "Graph View", color = RROnBackground) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = RROnBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RRBackground)
            )
        },
        containerColor = RRBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(RRBackground)
                .pointerInput(nodes.toList()) {
                    detectTapGestures { tapOffset ->
                        // Hit testing
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val actualTap = (tapOffset - center - offset) / scale
                        
                        val clickedNode = nodes.find { node ->
                            (node.position - actualTap).getDistance() < 25.dp.toPx() / scale
                        }
                        
                        if (clickedNode != null) {
                            viewModel.onNodeTapped(clickedNode.id)
                        } else {
                            viewModel.clearHighlight()
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val oldScale = scale
                        val newScale = (scale * zoom).coerceIn(0.1f, 10f)
                        
                        // To zoom into the centroid (where the fingers are), we need to adjust the offset
                        // Logic: offset = centroid - (centroid - offset) * (newScale / oldScale) + pan
                        offset = centroid - (centroid - offset) * (newScale / oldScale) + pan
                        scale = newScale
                    }
                }
        ) {
            val textMeasurer = rememberTextMeasurer()

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)

                // Highlighted set for efficiency
                val highlightedNodeIds = mutableSetOf<Long>()
                highlightedNodeId?.let { id ->
                    highlightedNodeIds.add(id)
                    edges.forEach { edge ->
                        if (edge.fromId == id) highlightedNodeIds.add(edge.toId)
                        if (edge.toId == id) highlightedNodeIds.add(edge.fromId)
                    }
                }

                // Draw Edges
                edges.forEach { edge ->
                    val fromNode = nodes.find { it.id == edge.fromId }
                    val toNode = nodes.find { it.id == edge.toId }
                    if (fromNode != null && toNode != null) {
                        val isHighlighted = highlightedNodeId != null && 
                            (edge.fromId == highlightedNodeId || edge.toId == highlightedNodeId)
                        
                        val color = if (isHighlighted) {
                            highlightColor ?: Color.Cyan
                        } else {
                            if (highlightedNodeId != null) edgeColor.copy(alpha = 0.05f) else edgeColor
                        }

                        drawLine(
                            color = color,
                            start = center + fromNode.position,
                            end = center + toNode.position,
                            strokeWidth = (if (isHighlighted) 2.dp else 1.dp).toPx() / scale
                        )
                    }
                }

                // Draw Nodes
                nodes.forEach { node ->
                    val isHighlighted = highlightedNodeIds.contains(node.id)
                    val baseAlpha = (1f / (node.level * 0.5f + 1f)).coerceIn(0.3f, 1f)
                    
                    val nodeColor = if (isHighlighted) {
                        highlightColor ?: Color.Cyan
                    } else {
                        val color = accent.color.copy(alpha = baseAlpha)
                        if (highlightedNodeId != null) color.copy(alpha = 0.1f) else color
                    }

                    val radius = if (node.isRoot) 8.dp.toPx() else 5.dp.toPx()

                    drawCircle(
                        color = nodeColor,
                        radius = radius * (if (isHighlighted) 1.2f else 1f),
                        center = center + node.position
                    )

                    if (scale > 0.4f || isHighlighted) {
                        val textResult = textMeasurer.measure(
                            text = node.label,
                            style = TextStyle(
                                color = if (isHighlighted) {
                                    (highlightColor ?: Color.Cyan).copy(alpha = 1f)
                                } else {
                                    labelColor.copy(alpha = if (highlightedNodeId != null) 0.1f else nodeColor.alpha)
                                },
                                fontSize = if (node.isRoot) 14.sp else 10.sp,
                                fontWeight = if (node.isRoot || isHighlighted) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        drawText(
                            textLayoutResult = textResult,
                            topLeft = center + node.position + Offset(-textResult.size.width / 2f, radius + 4.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}
