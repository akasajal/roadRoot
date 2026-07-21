package com.ishaan.roadroot.data.repository

import com.ishaan.roadroot.data.db.ProjectDao
import com.ishaan.roadroot.model.Project
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val dao: ProjectDao
) {
    fun getAllProjects(): Flow<List<Project>> = dao.getAllProjects()

    suspend fun getProjectById(id: Long): Project? = dao.getProjectById(id)

    suspend fun createProject(name: String, description: String = "", accentColor: Int): Long {
        return dao.insertProject(
            Project(name = name.trim(), description = description.trim(), accentColor = accentColor)
        )
    }

    suspend fun updateProject(project: Project) = dao.updateProject(project)

    suspend fun deleteProject(project: Project) = dao.deleteProject(project)

    suspend fun renameProject(project: Project, newName: String) {
        dao.updateProject(project.copy(name = newName.trim()))
    }

    suspend fun updateAccentColor(project: Project, accentColor: Int) {
        dao.updateProject(project.copy(accentColor = accentColor))
    }
}
