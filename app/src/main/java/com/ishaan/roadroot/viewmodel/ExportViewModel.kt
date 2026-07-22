package com.ishaan.roadroot.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.ProjectRepository
import com.ishaan.roadroot.data.repository.RoadmapRepository
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.RoadmapItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val projectRepo: ProjectRepository,
    private val roadmapRepo: RoadmapRepository
) : ViewModel() {

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus

    fun exportMarkdown(project: Project) {
        viewModelScope.launch {
            val items = roadmapRepo.getAllItemsForProject(project.id)
            val md = buildMarkdown(project, items)
            shareText(md, "${project.name}.md", "text/markdown")
        }
    }

    fun exportJson(project: Project) {
        viewModelScope.launch {
            val items = roadmapRepo.getAllItemsForProject(project.id)
            val json = buildJson(project, items).toString(2)
            shareText(json, "${project.name}.json", "application/json")
        }
    }

    fun backupAll() {
        viewModelScope.launch {
            val projects = projectRepo.getAllProjects().first()
            val root = JSONArray()
            projects.forEach { project ->
                val items = roadmapRepo.getAllItemsForProject(project.id)
                root.put(buildJson(project, items))
            }
            val json = root.toString(2)
            shareText(json, "roadroot-backup.json", "application/json")
        }
    }

    private fun buildMarkdown(project: Project, items: List<RoadmapItem>): String {
        val sb = StringBuilder()
        sb.appendLine("# ${project.name}")
        if (project.description.isNotBlank()) sb.appendLine("\n${project.description}\n")
        fun appendItem(item: RoadmapItem, depth: Int) {
            val indent = "  ".repeat(depth)
            val status = when (item.status.name) { "DONE" -> "[x]"; "IN_PROGRESS" -> "[-]"; else -> "[ ]" }
            sb.appendLine("$indent- $status ${item.title}")
            if (item.description.isNotBlank()) sb.appendLine("$indent  *${item.description}*")
            items.filter { it.parentId == item.id }.forEach { appendItem(it, depth + 1) }
        }
        items.filter { it.parentId == null }.forEach { appendItem(it, 0) }
        return sb.toString()
    }

    private fun buildJson(project: Project, items: List<RoadmapItem>): JSONObject {
        fun itemToJson(item: RoadmapItem): JSONObject {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("description", item.description)
            obj.put("status", item.status.name)
            item.dueDate?.let { obj.put("dueDate", it) }
            val children = JSONArray()
            items.filter { it.parentId == item.id }.forEach { children.put(itemToJson(it)) }
            obj.put("children", children)
            return obj
        }
        val proj = JSONObject()
        proj.put("id", project.id)
        proj.put("name", project.name)
        proj.put("description", project.description)
        val rootItems = JSONArray()
        items.filter { it.parentId == null }.forEach { rootItems.put(itemToJson(it)) }
        proj.put("items", rootItems)
        return proj
    }

    private fun shareText(content: String, filename: String, mimeType: String) {
        val file = File(context.cacheDir, filename)
        file.writeText(content)
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Export via").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
