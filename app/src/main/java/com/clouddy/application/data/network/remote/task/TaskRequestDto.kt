package com.clouddy.application.data.network.remote.task

data class TaskRequestDto(val tarea: String,
                          val isCompleted: Boolean,
                          val remoteId: String)
