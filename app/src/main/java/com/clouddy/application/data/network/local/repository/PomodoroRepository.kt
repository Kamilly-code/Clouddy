package com.clouddy.application.data.network.local.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.clouddy.application.core.utils.pomodoro.PomodoroState
import com.clouddy.application.data.network.local.dao.PomodoroDao
import com.clouddy.application.data.network.local.entity.Pomodoro
import com.clouddy.application.data.network.local.mapper.PomodoroMapper
import com.clouddy.application.data.network.remote.pomodoro.PomodoroApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepository @Inject constructor(
    private val pomodoroDao: PomodoroDao , private val api: PomodoroApiService
) {

    val allPomodoro: Flow<List<Pomodoro>> = pomodoroDao.getAllPomodoro()

    suspend fun insertPomodoro(pomodoro: Pomodoro) = withContext(Dispatchers.IO) {
        try {
            val request = PomodoroMapper.toRequest(pomodoro)
            val response = api.insertPomodoro(request)
            val syncedPomodoro = PomodoroMapper.fromResponse(response)
            pomodoroDao.insertPomodoro(syncedPomodoro)
        } catch (e: Exception) {
            Log.w("PomodoroRepository", "POST falhou, tentando update: ${e.message}")

            try {
                val request = PomodoroMapper.toRequest(pomodoro)
                val response = api.updatePomodoro(pomodoro.id, request)
                val updated = PomodoroMapper.fromResponse(response)
                pomodoroDao.insertPomodoro(updated)
            } catch (e: Exception) {
                Log.e("PomodoroRepository", "Update falhou também: ${e.message}")


                pomodoroDao.insertPomodoro(pomodoro)
            }
        }
    }

    suspend fun updateCurrentRound(round: Int) = withContext(Dispatchers.IO) {
        pomodoroDao.updatePomodoro(
            pomodoroDao.getPomodoroSettings().firstOrNull()?.copy(
                currentRound = round
            ) ?: return@withContext
        )
    }

    suspend fun updatePomodoro(pomodoro: Pomodoro) = withContext(Dispatchers.IO) {
        val request = PomodoroMapper.toRequest(pomodoro)
        val response = api.updatePomodoro(pomodoro.id ?: 0L, request)
        val updated = PomodoroMapper.fromResponse(response)
        pomodoroDao.updatePomodoro(updated)
    }

    fun getPomodoroSettings(): Flow<Pomodoro?> {
        return pomodoroDao.getPomodoroSettings()
    }

    suspend fun fetchPomodoroFromApiAndSave() {
        val response = api.getPomodoroSettings()
        if (response.isSuccessful) {
            val dto = response.body()
            dto?.let {
                val entity = PomodoroMapper.fromResponse(it)
                pomodoroDao.insertPomodoro(entity)
            }
        } else {
            Log.e("PomodoroRepository", "Erro ao buscar configurações: ${response.code()}")
        }
    }

    suspend fun deleteAll() {
        pomodoroDao.deleteAll()
    }

    suspend fun addFocusMinutes(minutes: Int) = pomodoroDao.addFocusMinutes(minutes)

    suspend fun initFocusTimeIfNeedes() {
        if (pomodoroDao.getTotalFocusTime().firstOrNull() == null) {
            pomodoroDao.insertPomodoro(
                Pomodoro(
                    id = 1,
                    focusTime = 25,
                    shortBreakTime = 5,
                    longBreakTime = 15,
                    rounds = 4,
                    totalMinutes = 0,
                    currentState = PomodoroState.IDLE,
                    currentRound = 0
                )
            )
        }
    }

}
