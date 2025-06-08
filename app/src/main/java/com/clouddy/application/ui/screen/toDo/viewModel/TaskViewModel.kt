package com.clouddy.application.ui.screen.toDo.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.clouddy.application.PreferencesManager
import com.clouddy.application.core.utils.NetworkUtils
import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.data.network.local.mapper.toTaskItem
import com.clouddy.application.data.network.local.repository.TaskRepository
import com.clouddy.application.domain.model.TaskItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.text.format
import kotlin.text.insert

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository : TaskRepository,
                                        private val preferencesManager: PreferencesManager) : ViewModel(){
    val tasks: StateFlow<List<TaskItem>> = repository.getAllTasks()
        .map { list -> list.map { it.toTaskItem() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    private val _isBackendOnline = MutableStateFlow(false)
    val isBackendOnline = _isBackendOnline.asStateFlow()

    init {
        checkAndSyncBackend()
    }


    fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val backendOnline = repository.isBackendAvailable()
            if (backendOnline) {
                repository.syncTasksWithServer()
            } else {
                Log.w("TaskViewModel", "Backend offline — usando somente dados locais")
            }
        }
    }

    fun syncAllTasks() {
        viewModelScope.launch {
            repository.syncTasksWithServer()
        }
    }


    fun addTask(task: Task) = viewModelScope.launch {
        val userId = preferencesManager.getUserId() ?: return@launch
        val formattedDate = formatDate(LocalDate.now())
        val taskWithDate = task.copy(
            remoteId = null,
            isSynced = false,
            date = formattedDate,
            userId = userId
        )
        repository.insertTaskRemoteAndLocal(taskWithDate)
    }


    fun insertOrUpdatedTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val isConnected = repository.isBackendAvailable()
            if (isConnected) {
                if (task.remoteId == null) {
                    repository.insertTaskRemoteAndLocal(task)
                }else if (task.isUpdated){
                    repository.updateTaskRemoteAndLocal(task)
                }else {
                    Log.d("TaskViewModel", "Tarefa já sincronizada — nenhuma ação necessária.")
                }
            } else {
                val localTask = if (task.remoteId == null) {
                    task.copy(isSynced = false)
                } else {
                    task.copy(isUpdated = true, isSynced = false)
                }
                repository.insert(localTask)

            }
        }
    }




    fun removeTask(task: Task, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val networkUtils = NetworkUtils()
            if (networkUtils.isConnected(context)) {
                repository.deleteTaskRemoteAndLocal(task)
            } else {
                val localTask = task.copy(isDeleted = true, isSynced = false)
                repository.updateTaskRemoteAndLocal(localTask)
            }
        }
    }



    fun storeTaskCompletation ( task: Task ) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                isCompleted = !task.isCompleted, // Alterna el estado de completado
                isUpdated = true, // Marca como actualizada
                isSynced = false // Marca como no sincronizada
            )

            if (task.remoteId == null) {
                repository.insert(updatedTask) // Inserta localmente si no tiene remoteId
            } else {
                repository.updateTaskRemoteAndLocal(updatedTask) // Actualiza local y remotamente
            }
        }
    }




    fun checkAndSyncBackend() {
        viewModelScope.launch {
            val online = repository.isBackendAvailable()
            _isBackendOnline.value = online
            if (online) {
                repository.syncTasksWithServer()
            }
        }
    }


    fun insertOrUpdateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val isConnected = repository.isBackendAvailable()

            if (isConnected) {
                if (task.remoteId == null) {
                    repository.insertTaskRemoteAndLocal(task)
                } else if (task.isUpdated) {
                    repository.updateTaskRemoteAndLocal(task)
                } else {
                    Log.d("TaskViewModel", "Tarefa já sincronizada — nenhuma ação necessária.")
                }
            } else {
                val localTask = if (task.remoteId == null) {
                    task.copy(isSynced = false)
                } else {
                    task.copy(isUpdated = true, isSynced = false)
                }

                repository.insert(localTask)
            }
        }
    }

    fun syncNotesIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncTasksWithServer()
        }
    }

    fun syncNotesWithServer() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncTasksWithServer()
        }
    }


    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        val formattedDate = formatDate(date)
        return repository.getTasksByDate(formattedDate)
    }



}