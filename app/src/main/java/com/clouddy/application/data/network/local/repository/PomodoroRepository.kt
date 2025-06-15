package com.clouddy.application.data.network.local.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.clouddy.application.PreferencesManager
import com.clouddy.application.core.utils.pomodoro.PomodoroState
import com.clouddy.application.data.network.local.dao.PomodoroDao
import com.clouddy.application.data.network.local.entity.Pomodoro
import com.clouddy.application.data.network.local.mapper.PomodoroMapper
import com.clouddy.application.data.network.remote.pomodoro.PomodoroApiService
import com.clouddy.application.data.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Singleton
class PomodoroRepository @Inject constructor(
    private val pomodoroDao: PomodoroDao , private val api: PomodoroApiService,
    private val preferencesManager: PreferencesManager,
    private val authRepository: AuthRepository
) {

    fun allPomodoro(): Flow<List<Pomodoro>> {
        val userId = preferencesManager.getUserId() ?: return flowOf(emptyList())
        return pomodoroDao.getAllPomodoro(userId)
    }

    private suspend fun getAuthToken(): String? {
        return try {
            val firebaseUser = authRepository.getCurrentUser() ?: return null
            // Correção: usar getIdToken() em vez de get()
            val tokenResult = firebaseUser.getIdToken(false).await()
            "Bearer ${tokenResult.token}"
        } catch (e: Exception) {
            Log.e("PomodoroRepository", "Failed to get auth token", e)
            null
        }
    }

    suspend fun insertPomodoro(pomodoro: Pomodoro) = withContext(Dispatchers.IO) {
        val userId = preferencesManager.getUserId() ?: return@withContext
        val safeRemoteId = pomodoro.remoteId ?: UUID.randomUUID().toString()

        val pomodoroWithId = pomodoro.copy(userId = userId, remoteId = safeRemoteId)


        try {
            val authToken = getAuthToken() ?: throw Exception("Not authenticated")
            val request = PomodoroMapper.toRequest((pomodoroWithId))
            val response = if (authToken != null) {
                api.insertPomodoro(request, authToken) // Assumindo que a API não requer auth para inserção
            } else {
                throw Exception("Not authenticated")
            }
            val syncedPomodoro = PomodoroMapper.fromResponse(response)
            pomodoroDao.insertPomodoro(syncedPomodoro)
        } catch (e: Exception) {
            Log.w("PomodoroRepository", "POST falhou, tentando update: ${e.message}")

            try {
                val authToken = getAuthToken() ?: throw Exception("Not authenticated")
                val request = PomodoroMapper.toRequest(pomodoroWithId)
                val response = if (authToken != null) {
                    api.updatePomodoro(safeRemoteId, request, authToken) // Assumindo que a API não requer auth para inserção
                } else {
                    throw Exception("Not authenticated")
                }
                val updated = PomodoroMapper.fromResponse(response)
                pomodoroDao.insertPomodoro(updated)
            } catch (e: Exception) {
                Log.e("PomodoroRepository", "Update falhou também: ${e.message}")
                pomodoroDao.insertPomodoro(pomodoroWithId)
            }
        }
    }

    suspend fun updateCurrentRound(round: Int) = withContext(Dispatchers.IO) {
        val userId = preferencesManager.getUserId() ?: return@withContext
        pomodoroDao.updatePomodoro(
            pomodoroDao.getPomodoroSettings(userId).firstOrNull()?.copy(
                currentRound = round
            ) ?: return@withContext
        )
    }

    suspend fun updatePomodoro(pomodoro: Pomodoro) = withContext(Dispatchers.IO) {
        val userId = preferencesManager.getUserId() ?: return@withContext
        val remoteId = pomodoro.remoteId ?: UUID.randomUUID().toString()
        val pomodoroWithId = pomodoro.copy(userId = userId, remoteId = remoteId)

        try {
            val authToken = getAuthToken() ?: throw Exception("Not authenticated")
            val request = PomodoroMapper.toRequest(pomodoroWithId)
            val response = api.updatePomodoro(remoteId, request, authToken)
            val updated = PomodoroMapper.fromResponse(response)
            pomodoroDao.updatePomodoro(updated)
        } catch (e: Exception) {
            Log.e("PomodoroRepository", "Update failed, saving locally", e)
            pomodoroDao.updatePomodoro(pomodoroWithId)
        }
    }


    fun getPomodoroSettings(userId: String): Flow<Pomodoro?> {
        return pomodoroDao.getPomodoroSettings(userId)
    }

    suspend fun fetchPomodoroFromApiAndSave() {
        val userId = preferencesManager.getUserId() ?: return
        try {
            val authToken = getAuthToken() ?: throw Exception("Not authenticated")
            val response = api.getPomodoroSettings(authToken) // Assumindo que o endpoint já inclui o auth token
            if (response.isSuccessful) {
                val dto = response.body()
                dto?.let {
                    val entity = PomodoroMapper.fromResponse(it)
                    pomodoroDao.insertPomodoro(entity.copy(userId = userId))
                }
            } else {
                Log.e("PomodoroRepository", "Error fetching settings: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("PomodoroRepository", "Failed to fetch from API", e)
        }
    }

    suspend fun deleteAll(userId: String) {
        pomodoroDao.deleteAll(userId)
    }

    suspend fun addFocusMinutes(minutes: Int, userId: String){
        pomodoroDao.addFocusMinutes(minutes, userId)
    }

    suspend fun initFocusTimeIfNeedes(userId: String) {
        if (pomodoroDao.getTotalFocusTime(userId).firstOrNull() == null) {
            pomodoroDao.insertPomodoro(
                Pomodoro(
                    id = 1,
                    focusTime = 25,
                    shortBreakTime = 5,
                    longBreakTime = 15,
                    rounds = 4,
                    totalMinutes = 0,
                    currentState = PomodoroState.IDLE,
                    currentRound = 0,
                    userId = userId

                )
            )
        }
    }
}
