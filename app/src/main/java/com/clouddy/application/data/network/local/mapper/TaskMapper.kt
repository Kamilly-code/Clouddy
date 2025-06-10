package com.clouddy.application.data.network.local.mapper

import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.domain.model.TaskItem

fun Task.toTaskItem(): TaskItem = TaskItem(
    id = this.id,
    remoteId = this.remoteId,
    task = this.task,
    date = this.date,
    isCompleted = this.isCompleted,
    isSynced = this.isSynced,
    isUpdated = this.isUpdated,
    isDeleted = this.isDeleted,
    userId = this.userId
)

fun TaskItem.toTask(userId: String): Task = Task(
    id = this.id,
    remoteId = this.remoteId,
    task = this.task,
    date = this.date,
    isCompleted = this.isCompleted,
    isSynced = this.isSynced,
    isDeleted = this.isDeleted,
    isUpdated = this.isUpdated,
    userId = userId

)