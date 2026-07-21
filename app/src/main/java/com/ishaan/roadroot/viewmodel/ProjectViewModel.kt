package com.ishaan.roadroot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.ProjectRepository
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.ProjectAccent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val repo: ProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<Project>> = repo.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createProject(name: String, accentColor: Int = ProjectAccent.GREEN.argb) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.createProject(name, accentColor = accentColor) }
    }

    fun renameProject(project: Project, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch { repo.renameProject(project, newName) }
    }

    fun updateDescription(project: Project, description: String) {
        viewModelScope.launch { repo.updateProject(project.copy(description = description)) }
    }

    fun updateAccentColor(project: Project, accent: ProjectAccent) {
        viewModelScope.launch { repo.updateAccentColor(project, accent.argb) }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch { repo.deleteProject(project) }
    }
}
