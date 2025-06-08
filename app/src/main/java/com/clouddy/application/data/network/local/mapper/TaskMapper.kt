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

fun TaskItem.toTask(userId: String): Task = Task(
    id = this.id,
    remoteId = this.remoteId,
    task = this.task,
    date = this.date,
    isCompleted = this.isCompleted,
    isSynced = false,
    isDeleted = false,
    isUpdated = false ,
    userId = userId

)