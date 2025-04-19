package com.clouddy.application.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clouddy.application.data.local.dao.TaskDao
import com.clouddy.application.data.local.entity.Task

@Database(entities = [Task::class], version = 2 , exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}