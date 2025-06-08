package com.clouddy.application.data.network.remote.pomodoro

data class PomodoroRequestDto(
    val focusTime: Int,
    val shortBreakTime: Int,
    val longBreakTime: Int,
    val rounds: Int,
    val totalMinutes: Int,
    val currentState: String,
    val currentRound: Int,
    val lastUpdatedDate: String,
    val userId: String
)
