package com.example.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.notesapp.screens.NoteEditScreen
import com.example.notesapp.screens.NoteListScreen
import com.example.notesapp.viewmodel.NoteViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: NoteViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {

        composable("list") {
            NoteListScreen(navController, viewModel)
        }

        composable("edit/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments
                ?.getString("noteId")
                ?.toIntOrNull() ?: -1

            NoteEditScreen(navController, viewModel, noteId)
        }
    }
}