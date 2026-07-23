package com.ishaan.roadroot.model

/**
 * A RoadmapItem decorated with computed progress composition.
 */
data class RoadmapItemWithProgress(
    val item: RoadmapItem,
    val doneProgress: Float,
    val discardedProgress: Float,
    val inProgressProgress: Float,
    val childCount: Int,
    val isLeaf: Boolean
) {
    val progress: Float get() = doneProgress + discardedProgress + inProgressProgress

    val effectiveStatus: ItemStatus
        get() = if (isLeaf) item.status else when {
            doneProgress >= 1f -> ItemStatus.DONE
            discardedProgress >= 1f -> ItemStatus.DISCARDED
            progress > 0f -> ItemStatus.IN_PROGRESS
            else -> ItemStatus.TODO
        }

    val progressPercent: Int get() = (progress * 100).toInt()
}
