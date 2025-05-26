package com.clouddy.application.data.network.local.repository

import android.util.Log
import com.clouddy.application.data.network.local.dao.PomodoroDao
import com.clouddy.application.data.network.local.entity.Pomodoro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun addFocusMinutes(minutes: Int) = pomodoroDao.addFocusMinutes(minutes)

    suspend fun initFocusTimeIfNeedes() {
        if (pomodoroDao.getTotalFocusTime().firstOrNull() == null){
            pomodoroDao.insertPomodoro(Pomodoro(focusTime = 25,
                shortBreakTime = 5,
                longBreakTime = 15,
                rounds = 4,
                totalMinutes = 0))
        }
    }

}
