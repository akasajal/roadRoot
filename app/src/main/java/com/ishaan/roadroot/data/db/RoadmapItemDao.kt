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

    /** All descendants of a project — used for progress calculation */
    @Query("SELECT * FROM roadmap_items WHERE projectId = :projectId")
    suspend fun getAllItemsForProject(projectId: Long): List<RoadmapItem>

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

    /** For duplicate: fetch all items under a given item recursively */
    @Query("SELECT * FROM roadmap_items WHERE parentId = :parentId")
    suspend fun getDirectChildren(parentId: Long): List<RoadmapItem>
}
