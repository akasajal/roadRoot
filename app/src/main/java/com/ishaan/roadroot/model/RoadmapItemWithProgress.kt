package com.ishaan.roadroot.model

/**
 * A RoadmapItem decorated with computed progress.
 * progress: 0.0 to 1.0
 * childCount: direct children count
 * isLeaf: true if this item has no children
 */
data class RoadmapItemWithProgress(
    val item: RoadmapItem,
    val progress: Float,
    val childCount: Int,
    val isLeaf: Boolean
) {
    val effectiveStatus: ItemStatus
        get() = if (isLeaf) item.status else when {
            progress >= 1f -> ItemStatus.DONE
            progress > 0f -> ItemStatus.IN_PROGRESS
            else -> ItemStatus.TODO
        }

    val progressPercent: Int get() = (progress * 100).toInt()
}
