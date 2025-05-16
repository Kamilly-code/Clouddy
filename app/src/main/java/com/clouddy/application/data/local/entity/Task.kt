package com.clouddy.application.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "task") val task: String,
    @ColumnInfo(name = "isCompleted") val isCompleted: Boolean = false,
    @ColumnInfo(name = "date") val date: String
)
