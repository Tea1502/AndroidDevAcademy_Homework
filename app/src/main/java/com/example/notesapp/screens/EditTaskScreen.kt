package com.example.notesapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.notesapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    navController: androidx.navigation.NavController,
    viewModel: com.example.notesapp.viewmodel.TaskViewModel,
    taskId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(taskId) {
        viewModel.loadTaskDetails(taskId)
    }

    LaunchedEffect(uiState.currentTask) {
        if (taskId != "-1" && uiState.currentTask != null) {
            title = uiState.currentTask!!.title
            description = uiState.currentTask!!.body
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { navController.popBackStack() }) { Text("Nazad") }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Naslov zadatka") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Opis zadatka") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.error != null) {
            Text(
                text = "Greška: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                viewModel.saveTask(
                    id = taskId,
                    title = title,
                    description = description,
                    onSuccess = {
                        navController.popBackStack()
                    },
                    onError = { porukaGreske ->
                        Toast.makeText(context, porukaGreske, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spremi na server")
        }
    }
}