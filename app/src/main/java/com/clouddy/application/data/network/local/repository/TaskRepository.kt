package com.clouddy.application.data.network.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.clouddy.application.PreferencesManager
import com.clouddy.application.data.network.local.dao.TaskDao
import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.data.network.remote.note.NoteRequestDto
import com.clouddy.application.data.network.remote.task.TaskApiService
import com.clouddy.application.data.network.remote.task.TaskRequestDto
import com.clouddy.application.data.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@ViewModelScoped
class TaskRepository @Inject constructor(private val taskDao: TaskDao,
                                         private val api: TaskApiService,
                                         private val preferencesManager: PreferencesManager,
                                         private val authRepository: AuthRepository) {
    fun getAllTasks(userId: String): Flow<List<Task>> {
        return taskDao.getAllTasks(userId)
    }

    suspend fun insert(task: Task, userId: String) = withContext(Dispatchers.IO) {
        val taskWithUser = task.copy(userId = userId)
        taskDao.insertNewTask(taskWithUser)
    }

    suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        taskDao.updateFull(task)
    }

    suspend fun delete(task: Task) = withContext(Dispatchers.IO) {
        taskDao.delete(task)
    }

    suspend fun insertTaskRemoteAndLocal(task: Task, userId: String) = withContext(Dispatchers.IO) {
        val token = getAuthToken() ?: return@withContext
        val userId = preferencesManager.getUserId() ?: return@withContext
        val generatedRemoteId = task.remoteId ?: UUID.randomUUID().toString()

        val localId = taskDao.insertNewTask(task.copy(remoteId = generatedRemoteId, isSynced = false))

        try {
            val dto = TaskRequestDto(task.task, task.isCompleted, generatedRemoteId, userId, date = task.date)
            val response = api.insertTask(dto, token)

            if (response.isSuccessful) {
                response.body()?.let { remote ->
                    // Atualiza a tarefa local com o remoteId e marca como sincronizada
                    val updated = task.copy(
                        id = localId,
                        remoteId = remote.remoteId, // Certifique-se que o backend retorna o remoteId
                        isSynced = true,
                        userId = userId
                    )
                    taskDao.updateFull(updated)
                }
            } else {
                Log.e("API", "Erro ao salvar tarefa no backend: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Erro ao conectar com servidor: ${e.message}")
        }
    }

    private suspend fun getAuthToken(): String? {
        return try {
            val firebaseUser = authRepository.getCurrentUser() ?: return null
            val tokenResult = firebaseUser.getIdToken(false).await()
            "Bearer ${tokenResult.token}"
        } catch (e: Exception) {
            Log.e("TaskRepository", "Failed to get auth token", e)
            null
        }
    }

    suspend fun updateTaskRemoteAndLocal(task: Task, userId: String) = withContext(Dispatchers.IO) {
        val token = getAuthToken() ?: return@withContext
        val userId = preferencesManager.getUserId() ?: return@withContext
        val dto = TaskRequestDto(task.task, task.isCompleted, task.remoteId ?: "", userId, date = task.date)
        if (task.id == null) return@withContext
        try {
            if (!task.remoteId.isNullOrEmpty()) {
                val dto = TaskRequestDto(task.task, task.isCompleted, task.remoteId!!, userId = task.userId, date = task.date)
                val response = api.updateTaskStatus(task.remoteId, task.isCompleted, dto, token)

                if (response.isSuccessful){
                    taskDao.updateFull(task.copy(isSynced = true, isUpdated = false))
                }else{
                    taskDao.updateFull(task.copy(isSynced = false, isUpdated = true))
                }
            } else {
                taskDao.updateFull(task.copy(isSynced = false, isUpdated = true))
            }

        } catch (e: Exception) {
            Log.e("API", "Erro ao atualizar tarefa: ${e.message}")
            taskDao.updateFull(task.copy(isSynced = false, isUpdated = true))
        }
    }

    suspend fun deleteTaskRemoteAndLocal(task: Task, userId: String) = withContext(Dispatchers.IO) {
        val userId = preferencesManager.getUserId() ?: return@withContext

        try {
            val token = getAuthToken() ?: return@withContext
            if (!task.remoteId.isNullOrEmpty()) {
                val response = api.deleteTask(task.remoteId, token)
                if (response.isSuccessful) {
                    taskDao.delete(task)
                } else {
                    Log.e("API", "Erro ao deletar no servidor: ${response.message()}")
                    taskDao.delete(task)
                }
            } else {
                taskDao.delete(task)
            }
        } catch (e: Exception) {
            Log.e("API", "Erro de conexão: ${e.message}")
            taskDao.delete(task)
        }
    }


    suspend fun isBackendAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://crud-production-60e8.up.railway.app/ping")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 1000
            connection.readTimeout = 1000
            connection.requestMethod = "GET"
            connection.connect()
            connection.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }



    suspend fun syncTasksWithServer(userId: String) = withContext(Dispatchers.IO) {
        val userId = preferencesManager.getUserId() ?: return@withContext
        val unsyncedTasks = taskDao.getAllUnsyncedTasks(userId).filter {
            !it.isDeleted && it.userId == userId && (it.remoteId.isNullOrEmpty())
        }
        for (task in unsyncedTasks) {
            try {
                val request = TaskRequestDto(
                    task.task,
                    task.isCompleted,
                    "",
                    userId,
                    date = task.date
                )
                val response = api.insertTask(request, getAuthToken() ?: "")

                if (response.isSuccessful) {
                    response.body()?.let { serverTask ->
                        val syncedTask = task.copy(
                            remoteId = serverTask.remoteId,
                            isSynced = true,
                            isUpdated = false
                        )
                        taskDao.updateFull(syncedTask)
                    }
                } else {
                    Log.e("Sync", "Erro ao criar tarefa: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao criar tarefa (id=${task.id}): ${e.message}")
            }
        }

        val updatedTasks = taskDao.getAllUpdatedTasks(userId).filter { !it.remoteId.isNullOrEmpty() && !it.isDeleted }
        for (task in updatedTasks) {
            try {
                val request = TaskRequestDto(
                    task.task,
                    task.isCompleted,
                    task.remoteId!!,
                    userId,
                    date = task.date
                )
                val response = api.updateTaskStatus(task.remoteId!!, task.isCompleted, request, getAuthToken() ?: "")

                if (response.isSuccessful) {
                    val syncedTask = task.copy(
                        isSynced = true,
                        isUpdated = false
                    )
                    taskDao.updateFull(syncedTask)
                } else {
                    Log.e("Sync", "Erro ao atualizar tarefa remota: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao atualizar tarefa (id=${task.id}): ${e.message}")
            }
        }

        val deletedTasks = taskDao.getAllDeletedTasks(userId).filter { !it.remoteId.isNullOrEmpty() }
        for (task in deletedTasks) {
            try {
                val response = api.deleteTask(task.remoteId!!, getAuthToken() ?: "")
                if (response.isSuccessful) {
                    taskDao.delete(task)
                } else {
                    Log.e("Sync", "Erro ao deletar tarefa remota: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao deletar tarefa (id=${task.id}): ${e.message}")
            }
        }


    }

    fun getTasksByDate(date: String, userId: String): Flow<List<Task>> {
        return taskDao.getTasksByDate(date, userId)
    }

}