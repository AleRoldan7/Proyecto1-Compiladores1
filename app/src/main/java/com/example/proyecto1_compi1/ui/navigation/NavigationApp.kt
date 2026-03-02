package com.example.proyecto1_compi1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1_compi1.ui.screens.EditScreen
import com.example.proyecto1_compi1.ui.screens.HomeScreen
import com.example.proyecto1_compi1.ui.screens.UploadFilesScreen

@Composable

fun NavigationApp() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("edit") {
            EditScreen(navController)
        }

        composable("upload") {
            UploadFilesScreen(navController)
        }

    }
}