package com.example.notesapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notesapp.viewmodel.TaskViewModel
import com.example.notesapp.model.Task

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(navController: androidx.navigation.NavController, viewModel: TaskViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAllTasks()
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Obriši zadatak") },
            text = { Text("Jeste li sigurni da želite obrisati zadatak '${taskToDelete?.title}'?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteTask(taskToDelete!!.id)
                    taskToDelete = null
                }) { Text("Obriši") }
            },
            dismissButton = { TextButton(onClick = { taskToDelete = null }) { Text("Odustani") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Moji Zadaci (API)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { navController.navigate("edit/-1") }, modifier = Modifier.fillMaxWidth()) {
            Text("Dodaj novi zadatak (+)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { navController.navigate("edit/${task.id}") },
                            onLongClick = { taskToDelete = task }
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text(task.body, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}