package com.clouddy.application.data.network.local.mapper

import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.domain.model.TaskItem

fun Task.toTaskItem(): TaskItem = TaskItem(
    id = this.id,
    remoteId = this.remoteId,
    task = this.task,
    date = this.date,
    isCompleted = this.isCompleted
)

fun TaskItem.toTask(): Task = Task(
    id = this.id,
    remoteId = this.remoteId,
    task = this.task,
    date = this.date,
    isCompleted = this.isCompleted,
    isSynced = false, // ajuste conforme necessário
    isDeleted = false, // ajuste conforme necessário
    isUpdated = false  // ajuste conforme necessário
)