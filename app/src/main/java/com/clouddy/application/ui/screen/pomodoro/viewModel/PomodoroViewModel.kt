package com.clouddy.application.ui.screen.pomodoro.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.network.local.entity.Pomodoro
import com.clouddy.application.data.network.local.repository.PomodoroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val repository: PomodoroRepository
) : ViewModel() {


    private val _pomodoroSettings = MutableStateFlow<Pomodoro?>(null)
    val pomodoroSettings: StateFlow<Pomodoro?> = _pomodoroSettings

    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound

    private val _isCycleFinished = MutableStateFlow(false)
    val isCycleFinished: StateFlow<Boolean> = _isCycleFinished

    init {
        viewModelScope.launch {
            repository.getPomodoroSettings().collect { settings ->
                _pomodoroSettings.value = settings
                resetRounds()
            }
        }
    }

    fun savePomodoroSettingsAndWait(
        focusTime: Int,
        shortBreakTime: Int,
        longBreakTime: Int,
        rounds: Int,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val current = repository.getPomodoroSettings().firstOrNull()

            val updatedPomodoro = Pomodoro(
                id = current?.id,
                focusTime = focusTime,
                shortBreakTime = shortBreakTime,
                longBreakTime = longBreakTime,
                rounds = rounds,
                totalMinutes = current?.totalMinutes ?: 0
            )

            repository.insertPomodoro(updatedPomodoro)

            _pomodoroSettings.value = updatedPomodoro
            resetRounds()
            _isCycleFinished.value = false


            Log.d("PomodoroViewModel", "Saved Pomodoro settings: $updatedPomodoro")

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun onPomodoroCompleted() {
        viewModelScope.launch {
            val settings = _pomodoroSettings.value ?: return@launch
            val totalRounds = settings.rounds


            repository.addFocusMinutes(settings.focusTime)

            // Verifica se terminou o último round
            if (_currentRound.value >= totalRounds) {
                _isCycleFinished.value = true
                Log.d("PomodoroViewModel", "Todos os rounds foram completados! 🎉")
                return@launch
            }

            // Avança para o próximo round
            _currentRound.value += 1
            Log.d("PomodoroViewModel", "Round de foco finalizado: ${_currentRound.value}/$totalRounds")
        }
    }

    fun onPlayPressed() {
        viewModelScope.launch {
            val totalRounds = _pomodoroSettings.value?.rounds ?: return@launch
            if (_currentRound.value >= totalRounds) {
                Log.d("PomodoroViewModel", "Ciclo completo. Reinicie para começar de novo.")
                return@launch
            }

            // Aqui inicia o temporizador (mas NÃO incrementa round ainda!)
            Log.d("PomodoroViewModel", "Iniciando round ${_currentRound.value + 1}")
        }
    }

    fun resetRounds() {
        _currentRound.value = 1
        _isCycleFinished.value = false
    }

    fun resetPomodoroSettings() {
        viewModelScope.launch {
            repository.deleteAll()
            _pomodoroSettings.value = Pomodoro(
                focusTime = 25,
                shortBreakTime = 5,
                longBreakTime = 15,
                rounds = 4
            )
            resetRounds()
        }
    }
}