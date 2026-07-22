package com.ishaan.roadroot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.ProjectRepository
import com.ishaan.roadroot.data.repository.RoadmapRepository
import com.ishaan.roadroot.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectStat(
    val project: Project,
    val total: Int,
    val done: Int,
    val discarded: Int
) {
    val percent: Int get() = if (total == 0) 0 else ((done + discarded) * 100 / total)
}

data class StatsUiState(
    val projectCount: Int = 0,
    val totalItems: Int = 0,
    val doneItems: Int = 0,
    val discardedItems: Int = 0,
    val projectStats: List<ProjectStat> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val projectRepo: ProjectRepository,
    private val roadmapRepo: RoadmapRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatsUiState())
    val state: StateFlow<StatsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            projectRepo.getAllProjects().collect { projects ->
                val stats = projects.map { project ->
                    val total = roadmapRepo.getTotalCount(project.id)
                    val done = roadmapRepo.getDoneCount(project.id)
                    val discarded = roadmapRepo.getDiscardedCount(project.id)

                    ProjectStat(
                        project = project,
                        total = total,
                        done = done,
                        discarded = discarded
                    )
                }
                _state.value = StatsUiState(
                    projectCount = projects.size,
                    totalItems = stats.sumOf { it.total },
                    doneItems = stats.sumOf { it.done },
                    discardedItems = stats.sumOf { it.discarded },
                    projectStats = stats,
                    isLoading = false
                )
            }
        }
    }
}
