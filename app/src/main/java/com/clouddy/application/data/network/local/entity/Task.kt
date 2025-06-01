package com.clouddy.application.data.network.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val remoteId: String? = null,
    @ColumnInfo(name = "task") val task: String,
    @ColumnInfo(name = "isCompleted") val isCompleted: Boolean = false,
    @ColumnInfo(name = "date") val date: String,
    val isSynced: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false
)
