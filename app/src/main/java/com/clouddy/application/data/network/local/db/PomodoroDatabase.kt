package com.clouddy.application.data.network.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.clouddy.application.core.utils.pomodoro.Converters
import com.clouddy.application.data.network.local.dao.PomodoroDao
import com.clouddy.application.data.network.local.entity.Pomodoro

@Database(entities = [Pomodoro::class], version = 10, exportSchema = true)
@TypeConverters(Converters::class)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
}