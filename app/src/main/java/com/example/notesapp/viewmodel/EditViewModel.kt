package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.notesapp.model.NoteRepository

class EditViewModel(private val repository: NoteRepository) : ViewModel() {
    fun saveNote(id: Int, title: String, description: String) {
        if (id == -1) {
            repository.addNote(title, description)
        } else {
            repository.updateNote(id, title, description)
        }
    }

    fun getNote(id: Int) = repository.getNoteById(id)
}