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
import com.ishaan.roadroot.ui.search.SearchScreen
import com.ishaan.roadroot.ui.settings.SettingsScreen
import com.ishaan.roadroot.ui.stats.StatsScreen
import com.ishaan.roadroot.viewmodel.ProjectViewModel

@Composable
fun RoadRootNavGraph() {
    val navController = rememberNavController()
    val projectViewModel: ProjectViewModel = hiltViewModel()
    val projects by projectViewModel.projects.collectAsState()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                viewModel = projectViewModel,
                onOpenProject = { projectId ->
                    val name = projects.find { it.id == projectId }?.name?.encode() ?: "Project"
                    navController.navigate("roadmap/$projectId/$name/-1")
                },
                onOpenSearch = { navController.navigate("search") },
                onOpenStats = { navController.navigate("stats") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }

        composable("search") {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onOpenItem = { projectId, projectName, itemId ->
                    navController.navigate("roadmap/$projectId/${projectName.encode()}/$itemId")
                }
            )
        }

        composable("stats") {
            StatsScreen(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = "roadmap/{projectId}/{projectName}/{itemId}",
            arguments = listOf(
                navArgument("projectId") { type = NavType.LongType },
                navArgument("projectName") { type = NavType.StringType },
                navArgument("itemId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val projectName = backStackEntry.arguments?.getString("projectName")?.decode() ?: "Project"
            RoadmapScreen(
                projectName = projectName,
                onBack = { navController.popBackStack() },
                onNavigateToItem = { projectId, itemId ->
                    navController.navigate("roadmap/$projectId/${projectName.encode()}/$itemId")
                }
            )
        }
    }
}

private fun String.encode() = java.net.URLEncoder.encode(this, "UTF-8")
private fun String.decode() = java.net.URLDecoder.decode(this, "UTF-8")
