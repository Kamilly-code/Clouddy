package com.clouddy.application.data.network.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.clouddy.application.data.network.local.dao.TaskDao
import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.data.network.remote.task.TaskApiService
import com.clouddy.application.data.network.remote.task.TaskRequestDto
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@ViewModelScoped
class TaskRepository @Inject constructor(private val dao: TaskDao, private val api: TaskApiService) {
    val tasks = dao.getAllTasks()

    suspend fun insert(task: Task) = withContext(Dispatchers.IO) {
        val localId = dao.insert(task.copy(isSynced = false))

        try {
            val dto = TaskRequestDto(task.task, task.isCompleted)
            val response = api.insertTask(dto)

            if (response.isSuccessful) {
                response.body()?.let { remote ->
                    val updated = task.copy(
                        id = localId.toInt(),
                        remoteId = remote.id.toString(),
                        isSynced = true
                    )
                    dao.update(updated.id, updated.isCompleted)
                }
            }
        } catch (e: Exception) {
            Log.e("API", "Erro ao inserir tarefa remota: ${e.message}")
        }
    }

    suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        if (task.remoteId.isNullOrEmpty()) {
            dao.update(task.id, task.isCompleted)
            return@withContext
        }

        try {
            val response = api.updateTaskStatus(task.remoteId.toLong(), task.isCompleted)
            if (response.isSuccessful) {
                dao.update(task.id, task.isCompleted)
            } else {
                Log.e("API", "Erro ao atualizar tarefa no servidor: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Falha ao atualizar tarefa: ${e.message}")
        }
    }

    suspend fun delete(task: Task) = withContext(Dispatchers.IO) {
        try {
            if (!task.remoteId.isNullOrEmpty()) {
                val response = api.deleteTask(task.remoteId.toLong())
                if (response.isSuccessful) {
                    dao.delete(task)
                } else {
                    Log.e("API", "Erro ao deletar tarefa no servidor: ${response.message()}")
                }
            } else {
                dao.delete(task)
            }
        } catch (e: Exception) {
            Log.e("API", "Erro de conexão ao deletar tarefa: ${e.message}")
        }
    }

    fun getTasksByDate(date: String): LiveData<List<Task>> = dao.getTasksByDate(date)


    suspend fun syncTasksWithServer() = withContext(Dispatchers.IO) {
        val unsyncedTasks = dao.getAllUnsyncedTasks()       // novas tarefas locais
        val updatedTasks = dao.getAllUpdatedTasks()         // tarefas modificadas localmente
        val deletedTasks = dao.getAllDeletedTasks()         // tarefas a excluir no backend

        // Inserir tarefas novas no backend
        unsyncedTasks.forEach { task ->
            try {
                val dto = TaskRequestDto(task.task, task.isCompleted)
                val response = api.insertTask(dto)
                if (response.isSuccessful) {
                    response.body()?.let { remote ->
                        val syncedTask = task.copy(
                            remoteId = remote.id.toString(),
                            isSynced = true
                        )
                        dao.updateFull(syncedTask)
                    }
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao sincronizar nova tarefa: ${e.message}")
            }
        }

        // Atualizar tarefas modificadas
        updatedTasks.forEach { task ->
            try {
                task.remoteId?.toLongOrNull()?.let { remoteId ->
                    val response = api.updateTaskStatus(remoteId, task.isCompleted)
                    if (response.isSuccessful) {
                        val updated = task.copy(isUpdated = false)
                        dao.updateFull(updated)
                    }
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao atualizar tarefa: ${e.message}")
            }
        }

        // Deletar tarefas marcadas como excluídas
        deletedTasks.forEach { task ->
            try {
                task.remoteId?.toLongOrNull()?.let { remoteId ->
                    val response = api.deleteTask(remoteId)
                    if (response.isSuccessful) {
                        dao.delete(task)
                    }
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao deletar tarefa do servidor: ${e.message}")
            }
        }
    }

}