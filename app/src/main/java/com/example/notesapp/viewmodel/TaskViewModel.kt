package com.example.notesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.api.SessionManager
import com.example.notesapp.api.TaskApiService
import com.example.notesapp.data.AppDatabase
import com.example.notesapp.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val currentTask: Task? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = TaskApiService.create()
    private val sessionManager = SessionManager(application)
    private val taskDao = AppDatabase.getDatabase(application).taskDao()

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            taskDao.getAllTasks().collect { lokalniZadaci ->
                _uiState.value = _uiState.value.copy(tasks = lokalniZadaci)
            }
        }

        viewModelScope.launch {
            sessionManager.authToken.collect { token ->
                if (!token.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                    loadAllTasks()
                }
            }
        }
    }

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.login(LoginRequest(username, password))
                sessionManager.saveAuthToken(response.token)
                _uiState.value = _uiState.value.copy(isLoggedIn = true, isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Login neuspješan: ${e.localizedMessage}")
            }
        }
    }

    fun loadAllTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val tokenNaDisku = sessionManager.authToken.firstOrNull()
                if (tokenNaDisku.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Niste prijavljeni.")
                    return@launch
                }

                val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"

                val response = apiService.getAllTasks(authHeader)

                taskDao.insertTasks(response.tasks)

                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Offline ste. Prikazani su lokalni podaci."
                )
            }
        }
    }

    fun loadTaskDetails(id: String) {
        if (id == "-1") {
            _uiState.value = _uiState.value.copy(currentTask = null)
            return
        }
        viewModelScope.launch {
            try {
                val tokenNaDisku = sessionManager.authToken.firstOrNull() ?: return@launch
                val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"
                val task = apiService.getTaskById(authHeader, id)
                _uiState.value = _uiState.value.copy(currentTask = task)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun saveTask(id: String, title: String, description: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null)

            val privremeniId = if (id == "-1") System.currentTimeMillis().toString() else id
            val lokalniTask = Task(id = privremeniId, title = title, body = description)

            try {
                taskDao.insertTasks(listOf(lokalniTask))

                onSuccess()

                val tokenNaDisku = sessionManager.authToken.firstOrNull()
                if (!tokenNaDisku.isNullOrEmpty()) {
                    val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"

                    if (id == "-1") {
                        apiService.createTask(authHeader, TaskRequest(title = title, body = description))

                    } else {
                        apiService.updateTask(authHeader, id, TaskRequest(title = title, body = description))
                    }

                    loadAllTasks()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Spremljeno lokalno (Offline). Sinkronizacija s mrežom nije uspjela.")
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            try {
                val tokenNaDisku = sessionManager.authToken.firstOrNull() ?: return@launch
                val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"
                apiService.deleteTask(authHeader, id)
                loadAllTasks()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            taskDao.deleteAllTasks()
            sessionManager.clearSession()
            _uiState.value = TaskUiState()
            onSuccess()
        }
    }
}