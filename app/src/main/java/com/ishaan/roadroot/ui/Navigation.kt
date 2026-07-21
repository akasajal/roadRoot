package com.ishaan.roadroot.ui

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ishaan.roadroot.ui.home.HomeScreen
import com.ishaan.roadroot.ui.roadmap.RoadmapScreen
import com.ishaan.roadroot.viewmodel.ProjectViewModel

@Composable
fun RoadRootNavGraph() {
    val navController = rememberNavController()
    // Shared ProjectViewModel so HomeScreen and nav logic can look up project names
    val projectViewModel: ProjectViewModel = hiltViewModel()
    val projects by projectViewModel.projects.collectAsState()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                viewModel = projectViewModel,
                onOpenProject = { projectId ->
                    val name = projects.find { it.id == projectId }?.name?.encode() ?: "Project"
                    navController.navigate("roadmap/$projectId/$name/-1")
                }
            )
        }

        composable(
            route = "roadmap/{projectId}/{projectName}/{itemId}",
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("projectName") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val projectName = backStackEntry.arguments
                ?.getString("projectName")
                ?.decode() ?: "Project"

            RoadmapScreen(
                projectName = projectName,
                onBack = { navController.popBackStack() },
                onNavigateToItem = { projectId, itemId ->
                    val name = projectName.encode()
                    navController.navigate("roadmap/$projectId/$name/$itemId")
                }
            )
        }
    }
}

private fun String.encode() = java.net.URLEncoder.encode(this, "UTF-8")
private fun String.decode() = java.net.URLDecoder.decode(this, "UTF-8")
