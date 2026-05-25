package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EditUiState(
    val note: Note? = null
)

class EditViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    fun loadNote(id: Int) {
        val note = repository.getNoteById(id)
        _uiState.value = EditUiState(note = note)
    }

    fun saveNote(id: Int, title: String, description: String) {
        if (id == -1) {
            repository.addNote(title, description)
        } else {
            repository.updateNote(id, title, description)
        }
    }
}