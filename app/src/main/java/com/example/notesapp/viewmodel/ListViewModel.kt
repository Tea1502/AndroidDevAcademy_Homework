package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.notesapp.model.NoteRepository

class ListViewModel(private val repository: NoteRepository) : ViewModel() {
    val notes = repository.notes
}