package com.example.notesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.api.SessionManager
import com.example.notesapp.api.TaskApiService
import com.example.notesapp.data.AppDatabase
import com.example.notesapp.model.*
import com.example.notesapp.util.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val currentTask: Task? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    application: Application,
    private val logger: AppLogger
) : AndroidViewModel(application) {

    private val apiService = TaskApiService.create()
    private val sessionManager = SessionManager(application)
    private val taskDao = AppDatabase.getDatabase(application).taskDao()

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState
        .map { state ->
            if (state.searchQuery.isEmpty()) {
                state
            } else {
                val filtriraniZadaci = state.tasks.filter { task ->
                    task.title.contains(state.searchQuery, ignoreCase = true) ||
                            (task.body?.contains(state.searchQuery, ignoreCase = true) == true)
                }
                state.copy(tasks = filtriraniZadaci)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskUiState()
        )

    init {
        logger.logI("TaskViewModel inicijaliziran pomoću Hilt DI-ja.")

        viewModelScope.launch {
            taskDao.getAllTasks().collect { lokalniZadaci ->
                logger.logD("Flow detektirao promjenu u Room bazi. Broj lokalnih taskova: ${lokalniZadaci.size}")
                _uiState.value = _uiState.value.copy(tasks = lokalniZadaci)
            }
        }

        viewModelScope.launch {
            sessionManager.authToken.collect { token ->
                if (!token.isNullOrEmpty()) {
                    logger.logI("Pronađen važeći token na disku. Postavljam login stanje.")
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                    loadAllTasks()
                } else {
                    logger.logW("Token ne postoji ili je obrisan.")
                }
            }
        }
    }

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            logger.logI("Pokrenut login za korisnika: $username")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = apiService.login(LoginRequest(username, password))
                sessionManager.saveAuthToken(response.token)
                _uiState.value = _uiState.value.copy(isLoggedIn = true, isLoading = false)
                logger.logI("Login uspješan. Token je spremljen.")
                onSuccess()
            } catch (e: Exception) {
                logger.logE("Greška prilikom logina: ${e.localizedMessage}")
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Login neuspješan: ${e.localizedMessage}")
            }
        }
    }

    fun loadAllTasks() {
        viewModelScope.launch {
            logger.logD("loadAllTasks() pozvan. Pokrećem sinkronizaciju s mrežom...")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val tokenNaDisku = sessionManager.authToken.firstOrNull()
                if (tokenNaDisku.isNullOrEmpty()) {
                    logger.logW("Dohvaćanje taskova prekinuto: Korisnik nema token.")
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Niste prijavljeni.")
                    return@launch
                }

                val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"

                val response = apiService.getAllTasks(authHeader)
                logger.logI("Mrežni podaci uspješno dohvaćeni sa servera. Broj taskova: ${response.tasks.size}")

                taskDao.insertTasks(response.tasks)
                logger.logD("Mrežni podaci prepisani u lokalnu Room bazu.")

                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                logger.logW("Mreža nedostupna. Prebacujem na lokalni prikaz iz baze. Razlog: ${e.localizedMessage}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Offline ste. Prikazani su lokalni podaci."
                )
            }
        }
    }

    fun loadTaskDetails(id: String) {
        if (id == "-1") {
            logger.logD("loadTaskDetails pozvan s ID -1. Otvara se prazna forma.")
            _uiState.value = _uiState.value.copy(currentTask = null)
            return
        }
        viewModelScope.launch {
            logger.logD("Dohvaćam detalje za task ID: $id")
            try {
                val tokenNaDisku = sessionManager.authToken.firstOrNull() ?: return@launch
                val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"
                val task = apiService.getTaskById(authHeader, id)
                _uiState.value = _uiState.value.copy(currentTask = task)
                logger.logI("Detalji taska uspješno učitani s mreže.")
            } catch (e: Exception) {
                logger.logE("Neuspješno dohvaćanje detalja taska: ${e.localizedMessage}")
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun saveTask(id: String, title: String, description: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            logger.logI("Pokrenuto spremanje taska (ID: $id, Naslov: $title)")
            _uiState.value = _uiState.value.copy(error = null)

            val privremeniId = if (id == "-1") System.currentTimeMillis().toString() else id
            val lokalniTask = Task(id = privremeniId, title = title, body = description)

            try {
                taskDao.insertTasks(listOf(lokalniTask))
                logger.logD("Task prvenstveno pohranjen u lokalnu Room bazu (ID: $privremeniId)")

                onSuccess()

                val tokenNaDisku = sessionManager.authToken.firstOrNull()
                if (!tokenNaDisku.isNullOrEmpty()) {
                    val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"

                    if (id == "-1") {
                        logger.logD("Šaljem novi task na server...")
                        apiService.createTask(authHeader, TaskRequest(title = title, body = description))
                    } else {
                        logger.logD("Šaljem ažurirani task na server...")
                        apiService.updateTask(authHeader, id, TaskRequest(title = title, body = description))
                    }

                    logger.logI("Task uspješno sinkroniziran sa serverom.")
                    loadAllTasks()
                }
            } catch (e: Exception) {
                logger.logW("Greška pri slanju na server (Korisnik je vjerojatno offline). Task ostaje u lokalnoj bazi: ${e.localizedMessage}")
                _uiState.value = _uiState.value.copy(error = "Spremljeno lokalno (Offline). Sinkronizacija s mrežom nije uspjela.")
            }
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            logger.logI("Pokrenuto brisanje taska ID: $id")
            try {
                val tokenNaDisku = sessionManager.authToken.firstOrNull() ?: return@launch
                val authHeader = if (tokenNaDisku.startsWith("Bearer ")) tokenNaDisku else "Bearer $tokenNaDisku"
                apiService.deleteTask(authHeader, id)
                logger.logI("Task uspješno obrisan sa servera.")
                loadAllTasks()
            } catch (e: Exception) {
                logger.logE("Brisanje taska nije uspjelo: ${e.localizedMessage}")
                _uiState.value = _uiState.value.copy(error = e.localizedMessage)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logger.logI("Korisnik je pokrenuo odjavu. Brišem lokalnu bazu i sesiju.")
            taskDao.deleteAllTasks()
            sessionManager.clearSession()
            _uiState.value = TaskUiState()
            logger.logI("Odjava uspješno izvršena.")
            onSuccess()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}