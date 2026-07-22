package com.ishaan.roadroot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ishaan.roadroot.ui.theme.*

data class RoadmapTemplate(
    val name: String,
    val icon: String,
    val items: List<String>
)

val TEMPLATES = listOf(
    RoadmapTemplate("Android App", "📱", listOf("Planning", "UI", "Backend", "Testing", "Publishing")),
    RoadmapTemplate("Novel", "📖", listOf("Outline", "Characters", "Draft", "Editing", "Publishing")),
    RoadmapTemplate("Portfolio", "🎨", listOf("Design", "Projects", "Deployment", "SEO", "Outreach")),
    RoadmapTemplate("Research", "🔬", listOf("Literature Review", "Methodology", "Data Collection", "Analysis", "Write-up")),
    RoadmapTemplate("Game", "🎮", listOf("Concept", "Prototype", "Core Loop", "Levels", "Polish", "Release")),
    RoadmapTemplate("Startup", "🚀", listOf("Idea Validation", "MVP", "Beta", "Launch", "Growth")),
    RoadmapTemplate("Blank", "⬜", emptyList())
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSheet(
    onDismiss: () -> Unit,
    onSelectTemplate: (RoadmapTemplate) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = RRSurfaceVariant,
        dragHandle = {
            Box(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp).width(36.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(RROnSurfaceSubtle))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Start from template", style = MaterialTheme.typography.headlineMedium, color = RROnBackground, modifier = Modifier.padding(bottom = 16.dp))
            HorizontalDivider(color = RRBorder)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(TEMPLATES) { template ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectTemplate(template); onDismiss() }
                            .padding(vertical = 14.dp, horizontal = 4.dp)
                    ) {
                        Text(template.icon, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(end = 14.dp))
                        Column {
                            Text(template.name, style = MaterialTheme.typography.titleLarge, color = RROnBackground)
                            if (template.items.isNotEmpty()) {
                                Text(
                                    template.items.take(3).joinToString(", ") + if (template.items.size > 3) "…" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RROnSurfaceMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
