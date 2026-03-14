package com.example.proyecto1_compi1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto1_compi1.ui.screens.AnswerScreen
import com.example.proyecto1_compi1.ui.screens.EditScreen
import com.example.proyecto1_compi1.ui.screens.HomeScreen
import com.example.proyecto1_compi1.ui.screens.PreviewScreen
import com.example.proyecto1_compi1.ui.screens.ServerScreen
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

        composable("preview") {
            PreviewScreen(navController)
        }

        composable("server") {
            ServerScreen(navController = navController)
        }

        composable("answer") {
            AnswerScreen(navController)
        }

        composable("upload") {
            UploadFilesScreen(navController)
        }
    }
}