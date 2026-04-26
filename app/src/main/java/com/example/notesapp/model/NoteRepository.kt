package com.example.notesapp.model

class NoteRepository {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> = _notes

    private var nextId = 1

    fun addNote(title: String, description: String) {

        _notes.add(Note(nextId++, title, description, "26.04.2026."))
    }

    fun updateNote(id: Int, title: String, description: String) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            _notes[index] = _notes[index].copy(title = title, description = description)
        }
    }

    fun getNoteById(id: Int) = _notes.find { it.id == id }
}