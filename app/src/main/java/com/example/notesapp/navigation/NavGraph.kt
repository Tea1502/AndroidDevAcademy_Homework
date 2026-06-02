package com.example.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.notesapp.screens.*
import com.example.notesapp.viewmodel.TaskViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, taskViewModel)
        }
        composable("list") {
            TaskListScreen(navController, taskViewModel)
        }
        composable("edit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: "-1"
            EditTaskScreen(navController, taskViewModel, taskId)
        }
    }
}