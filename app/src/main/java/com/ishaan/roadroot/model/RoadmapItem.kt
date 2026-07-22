package com.ishaan.roadroot.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ItemStatus {
    TODO, IN_PROGRESS, DONE, DISCARDED
}

@Entity(
    tableName = "roadmap_items",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("projectId"),
        Index("parentId")
    ]
)
data class RoadmapItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val parentId: Long?,
    val title: String,
    val description: String = "",
    val status: ItemStatus = ItemStatus.TODO,
    val dueDate: Long? = null,          // epoch millis, null = no due date
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
