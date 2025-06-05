package com.clouddy.application.ui.screen.pomodoro.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.core.utils.pomodoro.PomodoroState
import com.clouddy.application.data.network.local.entity.Pomodoro
import com.clouddy.application.data.network.local.repository.PomodoroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    private val _currentState = MutableStateFlow(PomodoroState.IDLE)
    val currentState: StateFlow<PomodoroState> = _currentState

    private val _isInBreak = MutableStateFlow(false)
    val isInBreak: StateFlow<Boolean> = _isInBreak

    init {
        viewModelScope.launch {
            repository.initFocusTimeIfNeedes()
            repository.getPomodoroSettings().collect { settings ->
                settings?.let {
                    _pomodoroSettings.value = it
                    _currentRound.value = it.currentRound
                    _currentState.value = it.currentState
                    _isCycleFinished.value = (it.currentRound >= it.rounds)
                }
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
            repository.deleteAll()
            val current = repository.getPomodoroSettings().firstOrNull()

            val updatedPomodoro = Pomodoro(
                id = 1,
                focusTime = focusTime,
                shortBreakTime = shortBreakTime,
                longBreakTime = longBreakTime,
                rounds = rounds,
                totalMinutes = current?.totalMinutes ?: 0,
                currentState = current?.currentState ?: PomodoroState.IDLE,
                currentRound = 0
            )

            repository.insertPomodoro(updatedPomodoro)

            _pomodoroSettings.value = updatedPomodoro
            _currentRound.value = 0
            _isCycleFinished.value = false


            Log.d("PomodoroViewModel", "Saved Pomodoro settings: $updatedPomodoro")

            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun onPomodoroCompleted(isBreak: Boolean) {
        viewModelScope.launch {
            val settings = _pomodoroSettings.value ?: return@launch
            val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

            if (!isBreak) {
                // Terminou o foco
                val updated = settings.copy(
                    totalMinutes = settings.totalMinutes + settings.focusTime,
                    lastUpdatedDate = today
                )
                repository.updatePomodoro(updated)
                _pomodoroSettings.value = updated

                // Decide qual break iniciar
                val isLastRound = (settings.currentRound + 1 >= settings.rounds)
                val nextState = if (isLastRound) PomodoroState.LONG_BREAK else PomodoroState.SHORT_BREAK
                updateCurrentState(nextState)
                _isInBreak.value = true
            } else {
                // Terminou o break - incrementa o round
                val nextRound = (settings.currentRound + 1) % (settings.rounds + 1)
                val updated = settings.copy(
                    currentRound = nextRound,
                    currentState = PomodoroState.FOCUS,
                    lastUpdatedDate = today
                )
                repository.updatePomodoro(updated)
                _pomodoroSettings.value = updated
                _currentRound.value = nextRound
                _isInBreak.value = false
                _isCycleFinished.value = (nextRound >= settings.rounds)
            }
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
        _currentRound.value = 0
        _isCycleFinished.value = false
    }

    fun updateCurrentState(newState: PomodoroState) {
        _currentState.value = newState

        viewModelScope.launch {
            val current = repository.getPomodoroSettings().firstOrNull()
            if (current != null) {
                val updated = current.copy(currentState = newState)
                repository.updatePomodoro(updated)
                _pomodoroSettings.value = updated
            }
        }
    }

    fun resetPomodoroSettings() {
        viewModelScope.launch {
            repository.deleteAll()
            _pomodoroSettings.value = Pomodoro(
                focusTime = 25,
                shortBreakTime = 5,
                longBreakTime = 15,
                rounds = 4,
                currentState = PomodoroState.IDLE,
                currentRound = 0
            )
            resetRounds()
        }
    }
}