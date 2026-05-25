package com.example.notesapp.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteRepository {
    private var nextId = 1

    private val _notesFlow = MutableStateFlow<List<Note>>(emptyList())
    val notesFlow: StateFlow<List<Note>> = _notesFlow.asStateFlow()

    fun addNote(title: String, description: String) {
        val currentList = _notesFlow.value.toMutableList()
        currentList.add(Note(nextId++, title, description, "25.05.2026."))
        _notesFlow.value = currentList
    }

    fun updateNote(id: Int, title: String, description: String) {
        val currentList = _notesFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(title = title, description = description)
            _notesFlow.value = currentList
        }
    }

    fun getNoteById(id: Int): Note? {
        return _notesFlow.value.find { it.id == id }
    }
}