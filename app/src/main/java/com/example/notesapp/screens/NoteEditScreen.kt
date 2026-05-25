package com.example.notesapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.notesapp.viewmodel.EditViewModel

@Composable
fun NoteEditScreen(
    navController: NavController,
    viewModel: EditViewModel,
    noteId: Int
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val existingNote = uiState.note

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }


    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(existingNote) {
        if (existingNote != null) {
            title = existingNote.title
            description = existingNote.description
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(onClick = { navController.popBackStack() }) {
            Text("Nazad")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Naslov") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth()
        )

        if (existingNote != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Datum kreiranja: ${existingNote.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveNote(noteId, title, description)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spremi")
        }
    }
}