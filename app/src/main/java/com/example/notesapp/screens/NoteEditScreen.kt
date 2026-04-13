package com.example.notesapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesapp.viewmodel.NoteViewModel

@Composable
fun NoteEditScreen(
    navController: NavController,
    viewModel: NoteViewModel,
    noteId: Int
) {

    val existingNote = viewModel.getNoteById(noteId)

    var title by remember { mutableStateOf(existingNote?.title ?: "") }
    var description by remember { mutableStateOf(existingNote?.description ?: "") }

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (noteId == -1) {
                    viewModel.addNote(title, description)
                } else {
                    viewModel.updateNote(noteId, title, description)
                }
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spremi")
        }
    }
}