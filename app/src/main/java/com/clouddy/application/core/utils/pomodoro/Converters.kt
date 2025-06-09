package com.clouddy.application.core.utils.pomodoro

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromPomodoroState(state: PomodoroState): String = state.name

    @TypeConverter
    fun toPomodoroState(value: String): PomodoroState = PomodoroState.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? = dateString?.let { LocalDate.parse(it) }

}
