package com.clouddy.application.data.network.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clouddy.application.data.network.local.dao.PomodoroDao
import com.clouddy.application.data.network.local.entity.Pomodoro

@Database(entities = [Pomodoro::class], version = 2, exportSchema = false)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
}