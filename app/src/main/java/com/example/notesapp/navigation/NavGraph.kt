package com.example.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.*
import com.example.notesapp.model.NoteRepository
import com.example.notesapp.screens.NoteEditScreen
import com.example.notesapp.screens.NoteListScreen
import com.example.notesapp.viewmodel.ListViewModel
import com.example.notesapp.viewmodel.EditViewModel

private val repository by lazy { NoteRepository() }

val ListViewModelFactory = viewModelFactory {
    initializer {
        ListViewModel(repository)
    }
}

val EditViewModelFactory = viewModelFactory {
    initializer {
        EditViewModel(repository)
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {

        composable("list") {
            val listViewModel: ListViewModel = viewModel(factory = ListViewModelFactory)
            NoteListScreen(navController, listViewModel)
        }

        composable("edit/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments
                ?.getString("noteId")
                ?.toIntOrNull() ?: -1

            val editViewModel: EditViewModel = viewModel(factory = EditViewModelFactory)
            NoteEditScreen(navController, editViewModel, noteId)
        }
    }
}