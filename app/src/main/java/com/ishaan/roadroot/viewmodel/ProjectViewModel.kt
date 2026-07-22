package com.ishaan.roadroot.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.ProjectRepository
import com.ishaan.roadroot.data.repository.RoadmapRepository
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.ProjectAccent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ProjectRepository,
    private val roadmapRepo: RoadmapRepository
) : ViewModel() {

    val projects: StateFlow<List<Project>> = repo.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Null = idle, non-null = message to show (success or error) */
    private val _importResult = MutableStateFlow<String?>(null)
    val importResult: StateFlow<String?> = _importResult

    fun clearImportResult() { _importResult.value = null }

    fun createProject(name: String, accentColor: Int = ProjectAccent.GREEN.argb) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.createProject(name, accentColor = accentColor) }
    }

    fun createProjectFromTemplate(name: String, accentColor: Int, templateItems: List<String>) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val projectId = repo.createProject(name, accentColor = accentColor)
            templateItems.forEachIndexed { index, title ->
                roadmapRepo.createItem(projectId, null, title)
            }
        }
    }

    /**
     * Reads a JSON file exported by ExportViewModel (single-project or backup array format)
     * and recreates the project with all its roadmap items.
     *
     * Supported JSON shapes:
     *  - Single project object: { "name": "...", "description": "...", "items": [...] }
     *  - Backup array: [ { "name": "...", ... }, ... ]  → imports the FIRST project only
     */
    fun importFromJson(uri: Uri) {
        viewModelScope.launch {
            try {
                val raw = context.contentResolver.openInputStream(uri)
                    ?.bufferedReader()
                    ?.readText()
                    ?: error("Could not read file")

                val trimmed = raw.trim()

                // Support both a bare object and a backup array (take first element)
                val projJson: JSONObject = if (trimmed.startsWith("[")) {
                    val arr = org.json.JSONArray(trimmed)
                    if (arr.length() == 0) error("JSON array is empty")
                    arr.getJSONObject(0)
                } else {
                    JSONObject(trimmed)
                }

                val name = projJson.optString("name").ifBlank { "Imported project" }
                val description = projJson.optString("description", "")
                val accentColor = ProjectAccent.GREEN.argb  // default; JSON doesn't store accent

                val projectId = repo.createProject(name, accentColor = accentColor)

                // Optionally persist description if the field exists
                if (description.isNotBlank()) {
                    val project = Project(id = projectId, name = name, description = description)
                    repo.updateDescription(project, description)
                }

                // Recursively insert items
                val items = projJson.optJSONArray("items")
                if (items != null) {
                    for (i in 0 until items.length()) {
                        insertItemRecursive(projectId, parentId = null, items.getJSONObject(i))
                    }
                }

                _importResult.value = "\"$name\" imported successfully"
            } catch (e: Exception) {
                _importResult.value = "Import failed: ${e.message}"
            }
        }
    }

    private suspend fun insertItemRecursive(
        projectId: Long,
        parentId: Long?,
        obj: JSONObject
    ) {
        val title = obj.optString("title", "Untitled")
        val description = obj.optString("description", "")
        val statusStr = obj.optString("status", "TODO")
        val dueDate: Long? = if (obj.has("dueDate")) obj.getLong("dueDate") else null

        val itemId = roadmapRepo.createItem(projectId, parentId, title, description)

        // Update status if not TODO
        val status = runCatching { ItemStatus.valueOf(statusStr) }.getOrDefault(ItemStatus.TODO)
        if (status != ItemStatus.TODO) {
            roadmapRepo.updateStatus(itemId, status)
        }
        if (dueDate != null) {
            roadmapRepo.updateDueDate(itemId, dueDate)
        }

        // Recurse into children
        val children = obj.optJSONArray("children")
        if (children != null) {
            for (i in 0 until children.length()) {
                insertItemRecursive(projectId, itemId, children.getJSONObject(i))
            }
        }
    }

    fun renameProject(project: Project, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch { repo.renameProject(project, newName) }
    }

    fun updateDescription(project: Project, description: String) {
        viewModelScope.launch { repo.updateDescription(project, description) }
    }

    fun updateAccentColor(project: Project, accent: ProjectAccent) {
        viewModelScope.launch { repo.updateAccentColor(project, accent.argb) }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch { repo.deleteProject(project) }
    }
}
