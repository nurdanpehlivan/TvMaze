package com.example.tvmaze.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tvmaze.viewmodel.ShowViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tvmaze.ui.screens.FavoriteScreen
import com.example.tvmaze.ui.screens.ShowDetailScreen
import com.example.tvmaze.ui.screens.ShowScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // ✅ TEK VM
    val vm: ShowViewModel = viewModel()

    NavHost(navController = navController, startDestination = "shows") {

        composable("shows") {
            ShowScreen(viewModel = vm, navController = navController)
        }

        composable("favorites") {
            FavoriteScreen(viewModel = vm, navController = navController)
        }

        composable("show_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            ShowDetailScreen(showId = id, viewModel = vm)
        }
    }
}


