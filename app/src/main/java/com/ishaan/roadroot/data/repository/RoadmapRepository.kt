package com.ishaan.roadroot.data.repository

import com.ishaan.roadroot.data.db.RoadmapItemDao
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.RoadmapItem
import com.ishaan.roadroot.model.RoadmapItemWithProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoadmapRepository @Inject constructor(
    private val dao: RoadmapItemDao
) {

    fun getRootItems(projectId: Long): Flow<List<RoadmapItem>> =
        dao.getRootItems(projectId)

    fun getChildItems(parentId: Long): Flow<List<RoadmapItem>> =
        dao.getChildItems(parentId)

    fun getItemFlow(itemId: Long): Flow<RoadmapItem?> =
        dao.getItemByIdFlow(itemId)

    fun getChildCountFlow(parentId: Long): Flow<Int> =
        dao.getChildCountFlow(parentId)

    suspend fun getItemById(id: Long): RoadmapItem? = dao.getItemById(id)

    suspend fun createItem(
        projectId: Long,
        parentId: Long?,
        title: String,
        description: String = ""
    ): Long {
        return dao.insertItem(
            RoadmapItem(
                projectId = projectId,
                parentId = parentId,
                title = title.trim(),
                description = description.trim()
            )
        )
    }

    suspend fun renameItem(id: Long, title: String) =
        dao.updateTitle(id, title.trim())

    suspend fun updateDescription(id: Long, description: String) =
        dao.updateDescription(id, description.trim())

    suspend fun updateStatus(id: Long, status: ItemStatus) =
        dao.updateStatus(id, status)

    suspend fun deleteItem(id: Long) = dao.deleteById(id)

    suspend fun duplicateItem(item: RoadmapItem, newParentId: Long?): Long {
        val newId = dao.insertItem(
            item.copy(
                id = 0,
                parentId = newParentId,
                title = "${item.title} (copy)",
                createdAt = System.currentTimeMillis()
            )
        )
        // Recursively duplicate children
        val children = dao.getDirectChildren(item.id)
        children.forEach { child ->
            duplicateItem(child, newId)
        }
        return newId
    }

    /**
     * Computes recursive progress for a single item.
     * Returns 0..1 float.
     */
    suspend fun computeProgress(itemId: Long, allItems: List<RoadmapItem>): Float {
        val children = allItems.filter { it.parentId == itemId }
        if (children.isEmpty()) {
            val item = allItems.find { it.id == itemId } ?: return 0f
            return when (item.status) {
                ItemStatus.DONE -> 1f
                ItemStatus.IN_PROGRESS -> 0.5f
                ItemStatus.TODO -> 0f
            }
        }
        val childProgress = children.map { computeProgress(it.id, allItems) }
        return childProgress.average().toFloat()
    }

    /** Get items for a scope (root or children) enriched with progress */
    suspend fun getItemsWithProgress(
        projectId: Long,
        parentId: Long?
    ): List<RoadmapItemWithProgress> {
        val allItems = dao.getAllItemsForProject(projectId)
        val scopeItems = if (parentId == null) {
            allItems.filter { it.parentId == null }
        } else {
            allItems.filter { it.parentId == parentId }
        }.sortedWith(compareBy({ it.sortOrder }, { it.createdAt }))

        return scopeItems.map { item ->
            val childCount = allItems.count { it.parentId == item.id }
            val isLeaf = childCount == 0
            val progress = if (isLeaf) {
                when (item.status) {
                    ItemStatus.DONE -> 1f
                    ItemStatus.IN_PROGRESS -> 0.5f
                    ItemStatus.TODO -> 0f
                }
            } else {
                computeProgress(item.id, allItems)
            }
            RoadmapItemWithProgress(
                item = item,
                progress = progress,
                childCount = childCount,
                isLeaf = isLeaf
            )
        }
    }
}
