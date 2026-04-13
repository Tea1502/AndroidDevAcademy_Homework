package com.example.notesapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesapp.viewmodel.NoteViewModel

@Composable
fun NoteListScreen(
    navController: NavController,
    viewModel: NoteViewModel
) {

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(
            onClick = { navController.navigate("edit/-1") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dodaj bilješku")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.notes) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("edit/${note.id}")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = note.description)
                    }
                }
            }
        }
    }
}