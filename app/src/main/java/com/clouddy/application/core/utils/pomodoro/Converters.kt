package com.clouddy.application.core.utils.pomodoro

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPomodoroState(state: PomodoroState): String = state.name

    @TypeConverter
    fun toPomodoroState(value: String): PomodoroState = PomodoroState.valueOf(value)
}