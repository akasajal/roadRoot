package com.ishaan.roadroot.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ishaan.roadroot.model.ProjectAccent
import com.ishaan.roadroot.ui.theme.*
import com.ishaan.roadroot.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenItem: (projectId: Long, projectName: String, itemId: Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(modifier = Modifier.fillMaxSize().background(RRBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = RROnSurface)
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(RRSurfaceVariant).padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (query.isEmpty()) Text("Search projects and items…", style = MaterialTheme.typography.bodyMedium, color = RROnSurfaceMuted)
                BasicTextField(
                    value = query, onValueChange = viewModel::onQueryChange,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = RROnBackground),
                    cursorBrush = SolidColor(RRAccent), singleLine = true, onTextLayout = {}
                )
            }
            if (query.isNotEmpty()) {
                IconButton(onClick = viewModel::clearQuery) {
                    Icon(Icons.Default.Close, "Clear", tint = RROnSurfaceMuted)
                }
            }
        }

        when {
            isSearching -> Box(Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RRAccent, modifier = Modifier.size(24.dp))
            }
            query.isNotEmpty() && results.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results for \"$query\"", style = MaterialTheme.typography.bodyMedium, color = RROnSurfaceMuted)
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results, key = { "${it.project.id}-${it.item?.id}" }) { result ->
                    val accent = ProjectAccent.fromArgb(result.project.accentColor)
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(RRSurfaceVariant).clickable {
                                onOpenItem(result.project.id, result.project.name, result.item?.id ?: -1L)
                            }.padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Column {
                            Text(result.project.name, style = MaterialTheme.typography.labelMedium, color = accent.color)
                            result.item?.let { item ->
                                Spacer(Modifier.height(4.dp))
                                Text(item.title, style = MaterialTheme.typography.titleLarge, color = RROnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                if (item.description.isNotBlank()) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(item.description, style = MaterialTheme.typography.bodySmall, color = RROnSurfaceMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
