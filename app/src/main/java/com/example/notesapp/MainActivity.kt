package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.navigation.NavGraph
import com.example.notesapp.ui.theme.NotesappTheme
import com.example.notesapp.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesappTheme {
                val taskViewModel: TaskViewModel = viewModel()

                NavGraph(taskViewModel = taskViewModel)
            }
        }
    }
}