package com.clouddy.application.data.local.repository

import android.util.Log
import com.clouddy.application.data.local.dao.PomodoroDao
import com.clouddy.application.data.local.entity.Pomodoro
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepository @Inject constructor(
    private val pomodoroDao: PomodoroDao
) {
    suspend fun insertPomodoro(pomodoro: Pomodoro) {
        Log.d("PomodoroRepository", "Inserting Pomodoro settings: $pomodoro")
        pomodoroDao.insertPomodoro(pomodoro)
    }

    suspend fun updatePomodoro(pomodoro: Pomodoro) {
        pomodoroDao.updatePomodoro(pomodoro)
    }

    fun getPomodoroSettings(): Flow<Pomodoro?> {
        return pomodoroDao.getPomodoroSettings()
    }

    suspend fun deleteAll() {
        pomodoroDao.deleteAll()
    }
}
