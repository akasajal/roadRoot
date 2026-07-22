package com.ishaan.roadroot.data.db

import androidx.room.*
import com.ishaan.roadroot.model.ItemStatus
import com.ishaan.roadroot.model.RoadmapItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadmapItemDao {

    @Query("SELECT * FROM roadmap_items WHERE projectId = :projectId AND parentId IS NULL ORDER BY sortOrder ASC, createdAt ASC")
    fun getRootItems(projectId: Long): Flow<List<RoadmapItem>>

    @Query("SELECT * FROM roadmap_items WHERE parentId = :parentId ORDER BY sortOrder ASC, createdAt ASC")
    fun getChildItems(parentId: Long): Flow<List<RoadmapItem>>

    @Query("SELECT * FROM roadmap_items WHERE id = :id")
    suspend fun getItemById(id: Long): RoadmapItem?

    @Query("SELECT * FROM roadmap_items WHERE id = :id")
    fun getItemByIdFlow(id: Long): Flow<RoadmapItem?>

    @Query("SELECT * FROM roadmap_items WHERE projectId = :projectId")
    suspend fun getAllItemsForProject(projectId: Long): List<RoadmapItem>

    /** Search by title across a project */
    @Query("SELECT * FROM roadmap_items WHERE projectId = :projectId AND title LIKE '%' || :query || '%' ORDER BY sortOrder ASC, createdAt ASC")
    suspend fun searchItems(projectId: Long, query: String): List<RoadmapItem>

    /** Search across ALL projects */
    @Query("SELECT * FROM roadmap_items WHERE title LIKE '%' || :query || '%' ORDER BY sortOrder ASC, createdAt ASC")
    suspend fun searchAllItems(query: String): List<RoadmapItem>

    @Query("SELECT COUNT(*) FROM roadmap_items WHERE parentId = :parentId")
    suspend fun getChildCount(parentId: Long): Int

    @Query("SELECT COUNT(*) FROM roadmap_items WHERE parentId = :parentId")
    fun getChildCountFlow(parentId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: RoadmapItem): Long

    @Update
    suspend fun updateItem(item: RoadmapItem)

    @Delete
    suspend fun deleteItem(item: RoadmapItem)

    @Query("DELETE FROM roadmap_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE roadmap_items SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: ItemStatus)

    @Query("UPDATE roadmap_items SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("UPDATE roadmap_items SET description = :description WHERE id = :id")
    suspend fun updateDescription(id: Long, description: String)

    @Query("UPDATE roadmap_items SET sortOrder = :order WHERE id = :id")
    suspend fun updateSortOrder(id: Long, order: Int)

    @Query("UPDATE roadmap_items SET dueDate = :dueDate WHERE id = :id")
    suspend fun updateDueDate(id: Long, dueDate: Long?)

    @Query("SELECT * FROM roadmap_items WHERE parentId = :parentId")
    suspend fun getDirectChildren(parentId: Long): List<RoadmapItem>

    @Query("SELECT * FROM roadmap_items WHERE projectId = :projectId ORDER BY dueDate ASC")
    suspend fun getItemsSortedByDueDate(projectId: Long): List<RoadmapItem>

    // Stats queries
    @Query("SELECT COUNT(*) FROM roadmap_items WHERE projectId = :projectId")
    suspend fun getTotalCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM roadmap_items WHERE projectId = :projectId AND status = 'DONE'")
    suspend fun getDoneCount(projectId: Long): Int

    @Query("SELECT COUNT(*) FROM roadmap_items WHERE projectId = :projectId AND parentId NOT IN (SELECT id FROM roadmap_items WHERE projectId = :projectId) OR (projectId = :projectId AND id NOT IN (SELECT DISTINCT parentId FROM roadmap_items WHERE parentId IS NOT NULL AND projectId = :projectId))")
    suspend fun getLeafCount(projectId: Long): Int
}
