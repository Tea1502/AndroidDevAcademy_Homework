package com.example.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.api.TaskApiService
import com.example.notesapp.api.TokenManager
import com.example.notesapp.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val currentTask: Task? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TaskViewModel : ViewModel() {
    private val apiService = TaskApiService.create()

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.login(LoginRequest(username, password))
                TokenManager.token = "Bearer ${response.token}"
                _uiState.value = _uiState.value.copy(isLoggedIn = true, isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Login neuspješan: ${e.localizedMessage}")
            }
        }
    }

    fun loadAllTasks() {
        val authHeader = TokenManager.token ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {

                val response = apiService.getAllTasks(authHeader)
                _uiState.value = _uiState.value.copy(tasks = response.tasks, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun loadTaskDetails(id: String) {
        if (id == "-1") {
            _uiState.value = _uiState.value.copy(currentTask = null)
            return
        }
        val authHeader = TokenManager.token ?: return
        viewModelScope.launch {
            try {
                val task = apiService.getTaskById(authHeader, id)
                _uiState.value = _uiState.value.copy(currentTask = task)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun saveTask(id: String, title: String, description: String, onSuccess: () -> Unit) {
        val authHeader = TokenManager.token ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)
            try {
                if (id == "-1") {
                    apiService.createTask(authHeader, TaskRequest(title = title, body = description))
                } else {
                    apiService.updateTask(authHeader, id, TaskRequest(title = title, body = description))
                }
                loadAllTasks()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Spremanje neuspješno: ${e.localizedMessage}")
            }
        }
    }

    fun deleteTask(id: String) {
        val authHeader = TokenManager.token ?: return
        viewModelScope.launch {
            try {
                apiService.deleteTask(authHeader, id)
                loadAllTasks()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }
}