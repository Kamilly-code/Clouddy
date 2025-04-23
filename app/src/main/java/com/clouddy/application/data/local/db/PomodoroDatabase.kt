package com.clouddy.application.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clouddy.application.data.local.dao.PomodoroDao
import com.clouddy.application.data.local.entity.Pomodoro

@Database(entities = [Pomodoro::class], version = 1, exportSchema = false)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
}