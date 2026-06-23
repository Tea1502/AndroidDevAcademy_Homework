package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notesapp.navigation.NavGraph
import com.example.notesapp.viewmodel.TaskViewModel
import com.example.notesapp.ui.theme.NotesappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val taskViewModel: TaskViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner.current?.let {
                    androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                } ?: androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )

            NotesappTheme {
                NavGraph(taskViewModel = taskViewModel)
            }
        }
    }
}