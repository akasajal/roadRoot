package com.ishaan.roadroot.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishaan.roadroot.data.repository.ProjectRepository
import com.ishaan.roadroot.data.repository.RoadmapRepository
import com.ishaan.roadroot.model.Project
import com.ishaan.roadroot.model.RoadmapItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sin

data class GraphNode(
    val id: Long,
    val label: String,
    val level: Int,
    var position: Offset = Offset.Zero,
    val isRoot: Boolean = false
)

data class GraphEdge(
    val fromId: Long,
    val toId: Long
)

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val projectRepo: ProjectRepository,
    private val roadmapRepo: RoadmapRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: Long = checkNotNull(savedStateHandle["projectId"])

    private val _project = MutableStateFlow<Project?>(null)
    val project = _project.asStateFlow()

    val nodes = mutableStateListOf<GraphNode>()
    val edges = mutableStateListOf<GraphEdge>()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val p = projectRepo.getProjectById(projectId)
            _project.value = p
            val allItems = roadmapRepo.getAllItemsForProject(projectId)
            
            val newNodes = mutableListOf<GraphNode>()
            val newEdges = mutableListOf<GraphEdge>()

            // Project Root Node (Level 0)
            val rootNodeId = -100L
            val rootNode = GraphNode(
                id = rootNodeId,
                label = p?.name ?: "Project",
                level = 0,
                position = Offset.Zero,
                isRoot = true
            )
            newNodes.add(rootNode)

            // Layout constants
            val baseLevelDistance = 400f
            
            // Calculate weights for all items
            val weights = mutableMapOf<Long, Int>()
            fun calculateWeight(itemId: Long): Int {
                val children = allItems.filter { it.parentId == itemId }
                val weight = if (children.isEmpty()) 1 else children.sumOf { calculateWeight(it.id) }
                weights[itemId] = weight
                return weight
            }

            val level1Items = allItems.filter { it.parentId == null }
            val totalWeight = level1Items.sumOf { calculateWeight(it.id) }

            // Level 1: Proportional distribution
            var currentAngle = 0f
            level1Items.forEach { item ->
                val itemWeight = weights[item.id] ?: 1
                val angleSlice = (itemWeight.toFloat() / totalWeight) * 2f * Math.PI.toFloat()
                
                // Position at the center of the slice
                val angle = currentAngle + angleSlice / 2f
                val pos = Offset(
                    x = cos(angle) * baseLevelDistance,
                    y = sin(angle) * baseLevelDistance
                )
                
                newNodes.add(GraphNode(id = item.id, label = item.title, level = 1, position = pos))
                newEdges.add(GraphEdge(fromId = rootNodeId, toId = item.id))

                layoutChildrenRecursive(
                    parentItem = item,
                    parentPos = pos,
                    parentAngle = angle,
                    parentSweep = angleSlice,
                    allItems = allItems,
                    weights = weights,
                    currentLevel = 1,
                    levelDistance = baseLevelDistance,
                    outNodes = newNodes,
                    outEdges = newEdges
                )

                currentAngle += angleSlice
            }

            nodes.clear()
            nodes.addAll(newNodes)
            edges.clear()
            edges.addAll(newEdges)
        }
    }

    private fun layoutChildrenRecursive(
        parentItem: RoadmapItem,
        parentPos: Offset,
        parentAngle: Float,
        parentSweep: Float,
        allItems: List<RoadmapItem>,
        weights: Map<Long, Int>,
        currentLevel: Int,
        levelDistance: Float,
        outNodes: MutableList<GraphNode>,
        outEdges: MutableList<GraphEdge>
    ) {
        val children = allItems.filter { it.parentId == parentItem.id }
        if (children.isEmpty()) return

        val nextLevel = currentLevel + 1
        val nextLevelDistance = parentPos.getDistance() + levelDistance
        val parentWeight = weights[parentItem.id] ?: 1
        
        // Children only use a portion of the parent's sweep to avoid overlap with siblings
        val childSweep = parentSweep * 0.85f 
        var currentChildAngle = parentAngle - childSweep / 2f

        children.forEach { child ->
            val childWeight = weights[child.id] ?: 1
            val childSlice = (childWeight.toFloat() / parentWeight) * childSweep
            
            val angle = currentChildAngle + childSlice / 2f
            val pos = Offset(
                x = cos(angle) * nextLevelDistance,
                y = sin(angle) * nextLevelDistance
            )

            outNodes.add(GraphNode(id = child.id, label = child.title, level = nextLevel, position = pos))
            outEdges.add(GraphEdge(fromId = parentItem.id, toId = child.id))

            layoutChildrenRecursive(
                parentItem = child,
                parentPos = pos,
                parentAngle = angle,
                parentSweep = childSlice,
                allItems = allItems,
                weights = weights,
                currentLevel = nextLevel,
                levelDistance = levelDistance * 0.8f, // Slightly shorter branches for deeper levels
                outNodes = outNodes,
                outEdges = outEdges
            )

            currentChildAngle += childSlice
        }
    }
}
