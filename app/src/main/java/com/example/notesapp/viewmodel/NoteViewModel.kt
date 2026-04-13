package com.example.notesapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.notesapp.model.Note

class NoteViewModel : ViewModel() {

    private var nextId = 3

    var notes = mutableStateListOf(
        Note(1, "Prva bilješka", "Opis 1"),
        Note(2, "Druga bilješka", "Opis 2")
    )
        private set

    fun addNote(title: String, description: String) {
        notes.add(Note(nextId++, title, description))
    }

    fun updateNote(id: Int, title: String, description: String) {
        val index = notes.indexOfFirst { it.id == id }
        if (index != -1) {
            notes[index] = notes[index].copy(
                title = title,
                description = description
            )
        }
    }

    fun getNoteById(id: Int): Note? {
        return notes.find { it.id == id }
    }
}
