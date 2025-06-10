package com.clouddy.application.domain.model

data class TaskItem(val id: Long? = null,
                    val remoteId: String? = null,
                    val task: String,
                    val isCompleted: Boolean = false,
                    val date: String,
                    val isSynced: Boolean = false,
                    val isUpdated: Boolean = false,
                    val isDeleted: Boolean = false,
                    val userId: String)
