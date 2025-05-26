package com.clouddy.application.data.network.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clouddy.application.data.network.local.dao.TaskDao
import com.clouddy.application.data.network.local.entity.Task

@Database(entities = [Task::class], version = 4 , exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}