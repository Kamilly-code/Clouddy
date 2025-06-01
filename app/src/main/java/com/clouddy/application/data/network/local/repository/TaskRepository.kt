package com.clouddy.application.data.network.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.clouddy.application.data.network.local.dao.TaskDao
import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.data.network.remote.task.TaskApiService
import com.clouddy.application.data.network.remote.task.TaskRequestDto
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@ViewModelScoped
class TaskRepository @Inject constructor(private val taskDao: TaskDao, private val api: TaskApiService) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) = withContext(Dispatchers.IO) {
        taskDao.insertNewTask(task)
    }

    suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        taskDao.updateFull(task)
    }

    suspend fun delete(task: Task) = withContext(Dispatchers.IO) {
        taskDao.delete(task)
    }

    suspend fun insertTaskRemoteAndLocal(task: Task) = withContext(Dispatchers.IO) {
        val localId = taskDao.insertNewTask(task.copy(isSynced = false))

        try {
            val dto = TaskRequestDto(task.task, task.isCompleted, "")
            val response = api.insertTask(dto)

            if (response.isSuccessful) {
                response.body()?.let { remote ->
                    val updated = task.copy(
                        id = localId,
                        remoteId = remote.id.toString(),
                        isSynced = true
                    )
                    taskDao.updateFull(updated)
            } }
        } catch (e: Exception) {
            Log.e("API", "Erro ao inserir tarefa remota: ${e.message}")
        }
    }

    suspend fun updateTaskRemoteAndLocal(task: Task) = withContext(Dispatchers.IO) {
        if (task.id == null) return@withContext
        try {
            if (!task.remoteId.isNullOrEmpty()) {
                val dto = TaskRequestDto(task.task, task.isCompleted, task.remoteId)
                val response = api.updateTaskStatus(task.remoteId, task.isCompleted, dto)

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

    suspend fun deleteTaskRemoteAndLocal(task: Task) = withContext(Dispatchers.IO) {
       try {
           if (task.remoteId != null && task.remoteId.isNotEmpty()) {
               val response = api.deleteTask(task.remoteId)
               if (response.isSuccessful) {
                   taskDao.delete(task)
               } else {
                   Log.e("API", "Erro ao deletar tarefa remota: ${response.message()}")
                   val markedTask = task.copy(isDeleted = true, isSynced = false)
                   taskDao.updateFull(markedTask)
               }
           } else {
               val markedTask = task.copy(isDeleted = true, isSynced = false)
               taskDao.updateFull(markedTask)
           }

       }catch (e: Exception) {
           Log.e("API", "Erro ao deletar tarefa: ${e.message}")

       }
    }

    suspend fun isBackendAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:4000/ping")
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



    suspend fun syncTasksWithServer() = withContext(Dispatchers.IO) {
        val unsyncedTasks = taskDao.getAllUnsyncedTasks().filter {!it.isDeleted }
        for (task in unsyncedTasks) {
            try {
                if (task.remoteId.isNullOrEmpty()) {
                    val request = TaskRequestDto(task.task, task.isCompleted, "")
                    val response = api.insertTask(request)

                    if (response.isSuccessful) {
                        response.body()?.let { serverTask ->
                            val syncedTask = task.copy(
                                remoteId = serverTask.id.toString(),
                                isSynced = true,
                                isUpdated = false
                            )
                            taskDao.updateFull(syncedTask)
                        }
                    } else {
                        Log.e("Sync", "Erro ao criar tarefa: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao criar tarefa (id=${task.id}): ${e.message}")
            }
        }

        val updatedTasks = taskDao.getAllUpdatedTasks().filter { !it.remoteId.isNullOrEmpty() && !it.isDeleted }
        for (task in updatedTasks) {
            try {
                val request = TaskRequestDto(task.task, task.isCompleted, task.remoteId!!)
                val response = api.updateTaskStatus(task.remoteId!!, task.isCompleted, request)

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

        val deletedTasks = taskDao.getAllDeletedTasks().filter { !it.remoteId.isNullOrEmpty() }
        for (task in deletedTasks) {
            try {
                if(!task.remoteId.isNullOrEmpty()) {
                    val response = api.deleteTask(task.remoteId!!)
                    if (response.isSuccessful) {
                        taskDao.delete(task)
                    } else {
                        Log.e("Sync", "Erro ao deletar tarefa remota: ${response.message()}")
                    }
                } else{
                    taskDao.delete(task)
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao deletar tarefa (id=${task.id}): ${e.message}")
            }
        }


    }

    fun getTasksByDate(date: String): Flow<List<Task>> = taskDao.getTasksByDate(date)
}