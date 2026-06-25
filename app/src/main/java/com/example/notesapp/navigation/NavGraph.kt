package com.example.notesapp.navigation

import androidx.compose.runtime.Composable
import com.example.notesapp.viewmodel.TaskViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notesapp.screens.EditTaskScreen
import com.example.notesapp.screens.LoginScreen
import com.example.notesapp.screens.TaskListScreen

@Composable
fun NavGraph(navController: NavHostController = androidx.navigation.compose.rememberNavController(), taskViewModel: TaskViewModel) {
    val uiState by taskViewModel.uiState.collectAsState()


    val startDestination = if (uiState.isLoggedIn) "tasks" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(navController = navController, viewModel = taskViewModel)
        }

        composable("tasks") {
            TaskListScreen(navController = navController, viewModel = taskViewModel)
        }

        composable("edit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: "-1"
            EditTaskScreen(navController = navController, viewModel = taskViewModel as com.example.notesapp.viewmodel.TaskViewModel, taskId = taskId)
        }
    }
}