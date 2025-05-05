package com.clouddy.application.ui.screen.pomodoro.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.local.entity.Pomodoro
import com.clouddy.application.data.local.repository.PomodoroRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val repository: PomodoroRepository
) : ViewModel() {


    private val _pomodoroSettings = MutableStateFlow<Pomodoro?>(null)
    val pomodoroSettings: StateFlow<Pomodoro?> = _pomodoroSettings

    init {
        viewModelScope.launch {
            repository.getPomodoroSettings().collect { settings ->
                _pomodoroSettings.value = settings
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
            val pomodoro = Pomodoro(
                focusTime = focusTime,
                shortBreakTime = shortBreakTime,
                longBreakTime = longBreakTime,
                rounds = rounds
            )
            repository.insertPomodoro(pomodoro)
            Log.d("PomodoroViewModel", "Saved Pomodoro settings: $pomodoro")
            _pomodoroSettings.value = repository.getPomodoroSettings().firstOrNull()
            onComplete()
        }
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
        }
    }
}