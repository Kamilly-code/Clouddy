package com.clouddy.application.data.local.repository

import androidx.lifecycle.LiveData
import com.clouddy.application.data.local.dao.TaskDao
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.data.local.entity.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val dao: TaskDao) {
    val tasks = dao.getAllTasks()

    suspend fun insert(task: Task) = dao.insert(task)
    suspend fun delete(task: Task) = dao.delete(task)
    suspend fun update(task: Task) = dao.update(task.id, task.isCompleted)
    fun getTasksByDate(date: String): LiveData<List<Task>> =  dao.getTasksByDate(date)
}