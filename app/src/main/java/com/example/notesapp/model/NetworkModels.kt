package com.example.notesapp.model

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)

data class Task(
    val id: String,
    val title: String,
    val body: String
)

data class TaskRequest(val title: String, val body: String)


data class TasksResponse(
    val tasks: List<Task>
)