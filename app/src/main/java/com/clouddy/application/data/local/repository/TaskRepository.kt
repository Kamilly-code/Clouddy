package com.clouddy.application.data.local.repository

import com.clouddy.application.data.local.dao.TaskDao
import com.clouddy.application.data.local.entity.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(private val dao: TaskDao) {
    val tasks = dao.getAllTasks()

    suspend fun insert(task: Task) = dao.insert(task)
    suspend fun delete(task: Task) = dao.delete(task)

}