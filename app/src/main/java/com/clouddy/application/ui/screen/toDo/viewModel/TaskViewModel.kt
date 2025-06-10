package com.clouddy.application.ui.screen.toDo.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.PreferencesManager
import com.clouddy.application.core.utils.NetworkUtils
import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.data.network.local.mapper.toTaskItem
import com.clouddy.application.data.network.local.repository.TaskRepository
import com.clouddy.application.data.repository.AuthRepository
import com.clouddy.application.domain.model.TaskItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository : TaskRepository,
                                        private val preferencesManager: PreferencesManager,
                                        private val authRepository: AuthRepository) : ViewModel(){
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _isBackendOnline = MutableStateFlow(false)
    val isBackendOnline = _isBackendOnline.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = getUserId() ?: run {
                Log.e("TaskViewModel", "UserId não encontrado")
                return@launch
            }
            _currentUserId.value = userId
            loadTasks(userId)
            checkAndSyncBackend(userId)
        }
    }

    private fun getUserId(): String? {
        val userId = preferencesManager.getUserId()
        if (userId.isNullOrEmpty()) {
            Log.e("TaskViewModel", "UserID vazio. Tentando recuperar do Firebase...")
            val firebaseUser = authRepository.getCurrentUser()
            firebaseUser?.uid?.let { uid ->
                preferencesManager.saveUserId(uid)
                return uid
            }
            return null
        }
        return userId
    }

    internal fun loadTasks(userId: String) {
        viewModelScope.launch {
            try {
                repository.getAllTasks(userId)
                    .map { tasks -> tasks.map { it.toTaskItem() } }
                    .collect { tasksList ->
                        _tasks.value = tasksList
                    }
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error loading tasks", e)
            }
        }
    }


    fun syncAllTasks() {
        currentUserId.value?.let { userId ->
            viewModelScope.launch {
                repository.syncTasksWithServer(userId)
            }
        }
    }

    fun addTask(task: Task) = viewModelScope.launch {
        currentUserId.value?.let { userId ->
            val taskWithUser = task.copy(
                remoteId = null,
                isSynced = false,
                userId = userId
            )
            repository.insertTaskRemoteAndLocal(taskWithUser, userId)
        }
    }

    fun insertOrUpdatedTask(task: Task) {
        currentUserId.value?.let { userId ->
            viewModelScope.launch(Dispatchers.IO) {
                val isConnected = repository.isBackendAvailable()
                if (isConnected) {
                    if (task.remoteId == null) {
                        repository.insertTaskRemoteAndLocal(task, userId)
                    } else if (task.isUpdated) {
                        repository.updateTaskRemoteAndLocal(task, userId)
                    } else {
                        Log.d("TaskViewModel", "Tarefa já sincronizada — nenhuma ação necessária.")
                    }
                } else {
                    val localTask = if (task.remoteId == null) {
                        task.copy(isSynced = false, userId = userId)
                    } else {
                        task.copy(isUpdated = true, isSynced = false, userId = userId)
                    }
                    repository.insert(localTask, userId)
                }
            }
        }
    }



    fun removeTask(task: Task, context: Context) {
        currentUserId.value?.let { userId ->
            viewModelScope.launch(Dispatchers.IO) {
                val taskWithUser = task.copy(userId = userId)
                val networkUtils = NetworkUtils()
                if (networkUtils.isConnected(context)) {
                    repository.deleteTaskRemoteAndLocal(taskWithUser, userId)
                } else {
                    val localTask = task.copy(isDeleted = true, isSynced = false, userId = userId)
                    repository.updateTaskRemoteAndLocal(localTask, userId)
                }
            }
        }
    }



    fun storeTaskCompletation (task: Task) {
        currentUserId.value?.let { userId ->
            viewModelScope.launch {
                val updatedTask = task.copy(
                    isCompleted = !task.isCompleted,
                    isUpdated = true,
                    isSynced = false,
                    userId = userId
                )

                if (task.remoteId == null) {
                    repository.insert(updatedTask, userId)
                } else {
                    repository.updateTaskRemoteAndLocal(updatedTask, userId)
                }
            }
        }
    }




    fun checkAndSyncBackend(userId: String) {
        viewModelScope.launch {
            val online = repository.isBackendAvailable()
            _isBackendOnline.value = online
            if (online) {
                repository.syncTasksWithServer(userId)
            }
        }
    }


    fun insertOrUpdateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val isConnected = repository.isBackendAvailable()

            if (isConnected) {
                if (task.remoteId == null) {
                    repository.insertTaskRemoteAndLocal(task, task.userId)
                } else if (task.isUpdated) {
                    repository.updateTaskRemoteAndLocal(task, task.userId)
                } else {
                    Log.d("TaskViewModel", "Tarefa já sincronizada — nenhuma ação necessária.")
                }
            } else {
                val localTask = if (task.remoteId == null) {
                    task.copy(isSynced = false)
                } else {
                    task.copy(isUpdated = true, isSynced = false)
                }

                repository.insert(localTask, task.userId)
            }
        }
    }

    fun syncNotesIfNeeded() {
        currentUserId.value?.let { userId ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.syncTasksWithServer(userId)
            }
        }
    }

    fun syncNotesWithServer() {
        currentUserId.value?.let { userId ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.syncTasksWithServer(userId)
            }
        }
    }



    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun getTasksByDate(date: LocalDate): Flow<List<Task>> {
        val formattedDate = formatDate(date)
        return currentUserId.value?.let { userId ->
            repository.getTasksByDate(formattedDate, userId)
        } ?: flowOf(emptyList())
    }
}