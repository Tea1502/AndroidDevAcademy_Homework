package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


data class ListUiState(
    val notes: List<Note> = emptyList()
)

class ListViewModel(private val repository: NoteRepository) : ViewModel() {

    val uiState: StateFlow<ListUiState> = repository.notesFlow
        .map { notesList -> ListUiState(notes = notesList) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListUiState()
        )
}