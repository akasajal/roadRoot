package com.ishaan.roadroot.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.RoadmapRepository
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.RoadmapItem
import com.ishaan.roadroot.model.RoadmapItemWithProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoadmapUiState(
    val currentItem: RoadmapItem? = null,  // null = root of project
    val children: List<RoadmapItemWithProgress> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RoadmapViewModel @Inject constructor(
    private val repo: RoadmapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: Long = checkNotNull(savedStateHandle["projectId"])
    private val itemId: Long? = savedStateHandle.get<Long>("itemId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    init {
        loadCurrentItem()
        observeChildren()
    }

    private fun loadCurrentItem() {
        viewModelScope.launch {
            val current = itemId?.let { repo.getItemById(it) }
            _uiState.update { it.copy(currentItem = current) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeChildren() {
        // Observe the child list reactively (triggers on DB changes)
        val childFlow = if (itemId == null) {
            repo.getRootItems(projectId)
        } else {
            repo.getChildItems(itemId)
        }

        viewModelScope.launch {
            childFlow.collect { _ ->
                refreshChildren()
            }
        }
    }

    fun refreshChildren() {
        viewModelScope.launch {
            val items = repo.getItemsWithProgress(projectId, itemId)
            _uiState.update { it.copy(children = items, isLoading = false) }
        }
    }

    fun addItem(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repo.createItem(projectId, itemId, title)
        }
    }

    fun renameItem(id: Long, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch { repo.renameItem(id, newTitle) }
    }

    fun updateItemDescription(id: Long, description: String) {
        viewModelScope.launch { repo.updateDescription(id, description) }
    }

    fun updateCurrentDescription(description: String) {
        val current = _uiState.value.currentItem ?: return
        viewModelScope.launch {
            repo.updateDescription(current.id, description)
            _uiState.update { it.copy(currentItem = current.copy(description = description)) }
        }
    }

    fun updateStatus(id: Long, status: ItemStatus) {
        viewModelScope.launch { repo.updateStatus(id, status) }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch { repo.deleteItem(id) }
    }

    fun duplicateItem(item: RoadmapItem) {
        viewModelScope.launch { repo.duplicateItem(item, item.parentId) }
    }
}
