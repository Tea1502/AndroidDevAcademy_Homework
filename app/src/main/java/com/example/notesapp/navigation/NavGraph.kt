package com.example.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.notesapp.screens.NoteEditScreen
import com.example.notesapp.screens.NoteListScreen
import com.example.notesapp.viewmodel.ListViewModel
import com.example.notesapp.viewmodel.EditViewModel
import com.example.notesapp.model.NoteRepository
private val repository by lazy { NoteRepository() }

@Composable
fun NavGraph() {
    val navController = rememberNavController()


    val listViewModel = ListViewModel(repository)
    val editViewModel = EditViewModel(repository)

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            NoteListScreen(navController, listViewModel)
        }
        composable("edit/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: -1
            NoteEditScreen(navController, editViewModel, noteId)
        }
    }
}