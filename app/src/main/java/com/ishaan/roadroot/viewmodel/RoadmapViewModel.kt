package com.ishaan.roadroot.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.ProjectRepository
import com.ishaan.roadroot.data.repository.RoadmapRepository
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.RoadmapItem
import com.ishaan.roadroot.model.RoadmapItemWithProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoadmapUiState(
    val currentItem: RoadmapItem? = null,
    val project: Project? = null,
    val children: List<RoadmapItemWithProgress> = emptyList(),
    // visual tree: map of itemId → direct child titles (read-only preview)
    val childPreview: Map<Long, List<String>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val repo: RoadmapRepository,
    private val projectRepo: ProjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val projectId: Long = checkNotNull(savedStateHandle["projectId"])
    private val itemId: Long? = savedStateHandle.get<Long>("itemId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    init {
        loadContext()
        observeChildren()
    }

    private fun loadContext() {
        viewModelScope.launch {
            val current = itemId?.let { repo.getItemById(it) }
            val project = projectRepo.getProjectById(projectId)
            _uiState.update { it.copy(currentItem = current, project = project) }
        }
    }

    private fun observeChildren() {
        val childFlow = if (itemId == null) repo.getRootItems(projectId)
                        else repo.getChildItems(itemId)
        viewModelScope.launch {
            childFlow.collect { refreshChildren() }
        }
    }

    fun refreshChildren() {
        viewModelScope.launch {
            val items = repo.getItemsWithProgress(projectId, itemId)
            // For each non-leaf item, fetch direct child titles for visual tree
            val preview = mutableMapOf<Long, List<String>>()
            items.filter { !it.isLeaf }.forEach { parent ->
                val children = repo.getDirectChildrenOf(parent.item.id)
                preview[parent.item.id] = children.map { it.title }
            }
            _uiState.update { it.copy(children = items, childPreview = preview, isLoading = false) }
        }
    }

    fun addItem(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { repo.createItem(projectId, itemId, title) }
    }

    fun renameItem(id: Long, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch { repo.renameItem(id, newTitle) }
    }

    fun updateCurrentDescription(description: String) {
        viewModelScope.launch {
            if (itemId == null) {
                // We're at project root — update project description
                val project = _uiState.value.project ?: return@launch
                projectRepo.updateDescription(project, description)
                _uiState.update { it.copy(project = project.copy(description = description)) }
            } else {
                val current = _uiState.value.currentItem ?: return@launch
                repo.updateDescription(current.id, description)
                _uiState.update { it.copy(currentItem = current.copy(description = description)) }
            }
        }
    }

    fun updateStatus(id: Long, status: ItemStatus) {
        viewModelScope.launch { repo.updateStatus(id, status) }
    }

    fun updateDueDate(id: Long, dueDate: Long?) {
        viewModelScope.launch { repo.updateDueDate(id, dueDate) }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch { repo.deleteItem(id) }
    }

    fun duplicateItem(item: RoadmapItem) {
        viewModelScope.launch { repo.duplicateItem(item, item.parentId) }
    }

    fun reorderItems(reordered: List<RoadmapItemWithProgress>) {
        viewModelScope.launch {
            repo.updateSortOrders(reordered.map { it.item })
            _uiState.update { it.copy(children = reordered) }
        }
    }
}
