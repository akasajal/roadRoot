package com.ishaan.roadroot.ui.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishaan.roadroot.model.ProjectAccent
import com.ishaan.roadroot.ui.theme.*
import com.ishaan.roadroot.viewmodel.ProjectStat
import com.ishaan.roadroot.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val overallPercent = if (state.totalItems == 0) 0f else state.doneItems.toFloat() / state.totalItems

    Column(modifier = Modifier.fillMaxSize().background(RRBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = RROnSurface)
            }
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text("Statistics", style = MaterialTheme.typography.labelMedium, color = RROnSurfaceMuted)
                Text("Overview", style = MaterialTheme.typography.headlineMedium, color = RROnBackground)
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overall ring
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                        .background(RRSurfaceVariant).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        OverallRing(progress = overallPercent)
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCell(label = "Projects", value = "${state.projectCount}")
                            StatCell(label = "Total Items", value = "${state.totalItems}")
                            StatCell(label = "Completed", value = "${state.doneItems}")
                            StatCell(label = "Discarded",value = "${state.discardedItems}")
                        }
                    }
                }
            }

            // Per-project bars
            if (state.projectStats.isNotEmpty()) {
                item {
                    Text("BY PROJECT", style = MaterialTheme.typography.labelMedium, color = RROnSurfaceMuted, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                }
                items(state.projectStats, key = { it.project.id }) { stat ->
                    ProjectStatCard(stat = stat)
                }
            }
        }
    }
}

@Composable
private fun OverallRing(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress, animationSpec = tween(1000), label = "ring"
    )
    Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = RRSurfaceElevated,
            strokeWidth = 10.dp,
            strokeCap = StrokeCap.Round
        )
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            color = RRStatusDone,
            strokeWidth = 10.dp,
            strokeCap = StrokeCap.Round
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(animatedProgress * 100).toInt()}%", style = MaterialTheme.typography.headlineLarge, color = RROnBackground)
            Text("complete", style = MaterialTheme.typography.labelSmall, color = RROnSurfaceMuted)
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = RROnBackground)
        Text(label, style = MaterialTheme.typography.labelSmall, color = RROnSurfaceMuted)
    }
}

@Composable
private fun ProjectStatCard(stat: ProjectStat) {
    val accent = ProjectAccent.fromArgb(stat.project.accentColor)
    val animatedProgress by animateFloatAsState(
        targetValue = stat.percent / 100f, animationSpec = tween(800), label = "bar"
    )
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(RRSurfaceVariant).padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accent.color))
                Spacer(Modifier.width(8.dp))
                Text(stat.project.name, style = MaterialTheme.typography.titleLarge, color = RROnBackground, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${stat.done}/${stat.total}", style = MaterialTheme.typography.labelSmall, color = RROnSurfaceMuted)
                Spacer(Modifier.width(8.dp))
                Text("${stat.percent}%", style = MaterialTheme.typography.labelSmall, color = accent.color)
            }
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(RRSurfaceElevated)) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(animatedProgress).clip(RoundedCornerShape(2.dp)).background(accent.color))
            }
        }
    }
}
