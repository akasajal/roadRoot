package com.ishaan.roadroot.ui.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.ishaan.roadroot.viewmodel.GraphNode
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
    val accent = project?.let { ProjectAccent.fromArgb(it.accentColor) } ?: ProjectAccent.GREEN
    val edgeColor = RROnSurfaceSubtle.copy(alpha = 0.2f)
    val labelColor = RROnBackground

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

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
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        offset += dragAmount
                    }
                }
                .transformable(state = transformState)
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

                // Draw Edges
                edges.forEach { edge ->
                    val fromNode = nodes.find { it.id == edge.fromId }
                    val toNode = nodes.find { it.id == edge.toId }
                    if (fromNode != null && toNode != null) {
                        drawLine(
                            color = edgeColor,
                            start = center + fromNode.position,
                            end = center + toNode.position,
                            strokeWidth = 1.dp.toPx() / scale
                        )
                    }
                }

                // Draw Nodes
                nodes.forEach { node ->
                    val nodeColor = accent.color.copy(
                        alpha = (1f / (node.level * 0.5f + 1f)).coerceIn(0.3f, 1f)
                    )
                    val radius = if (node.isRoot) 8.dp.toPx() else 5.dp.toPx()

                    drawCircle(
                        color = nodeColor,
                        radius = radius,
                        center = center + node.position
                    )

                    if (scale > 0.4f) {
                        val textResult = textMeasurer.measure(
                            text = node.label,
                            style = TextStyle(
                                color = labelColor.copy(alpha = nodeColor.alpha),
                                fontSize = if (node.isRoot) 14.sp else 10.sp,
                                fontWeight = if (node.isRoot) FontWeight.Bold else FontWeight.Normal
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
