package com.example.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

@Entity(tableName = "tasks_table")
data class Task(
    @PrimaryKey
    val id: String,
    val title: String,
    val body: String
)

data class TaskRequest(val title: String, val body: String)

data class TasksResponse(
    val tasks: List<Task>
)