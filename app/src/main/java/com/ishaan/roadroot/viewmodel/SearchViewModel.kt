package com.ishaan.roadroot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.ProjectRepository
import com.ishaan.roadroot.data.repository.RoadmapRepository
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.RoadmapItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResult(
    val project: Project,
    val item: RoadmapItem?   // null = the project itself matched
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val projectRepo: ProjectRepository,
    private val roadmapRepo: RoadmapRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results: StateFlow<List<SearchResult>> = _results.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Cache projects for lookup
    private var allProjects: List<Project> = emptyList()

    init {
        viewModelScope.launch {
            projectRepo.getAllProjects().collect { allProjects = it }
        }
        setupSearch()
    }

    @OptIn(FlowPreview::class)
    private fun setupSearch() {
        viewModelScope.launch {
            _query
                .debounce(200)
                .collect { q ->
                    if (q.isBlank()) {
                        _results.value = emptyList()
                        return@collect
                    }
                    _isSearching.value = true
                    val results = mutableListOf<SearchResult>()

                    // Match projects by name
                    projectRepo.searchProjects(q).forEach { project ->
                        results.add(SearchResult(project, null))
                    }

                    // Match roadmap items across all projects
                    roadmapRepo.searchAllItems(q).forEach { item ->
                        val project = allProjects.find { it.id == item.projectId } ?: return@forEach
                        // Don't double-add if project already matched and item title same
                        results.add(SearchResult(project, item))
                    }

                    _results.value = results.distinctBy { "${it.project.id}-${it.item?.id}" }
                    _isSearching.value = false
                }
        }
    }

    fun onQueryChange(q: String) { _query.value = q }
    fun clearQuery() { _query.value = "" }
}
