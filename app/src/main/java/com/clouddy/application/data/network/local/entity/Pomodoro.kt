package com.clouddy.application.data.network.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.clouddy.application.core.utils.pomodoro.PomodoroState


@Entity(tableName = "pomodoro_table")
data class Pomodoro(
    @PrimaryKey val id: Long = 1,
    @ColumnInfo(name = "focusTime") val focusTime: Int,
    @ColumnInfo(name = "shortBreakTime") val shortBreakTime: Int,
    @ColumnInfo(name = "longBreakTime") val longBreakTime: Int,
    @ColumnInfo(name = "rounds") val rounds: Int,
    @ColumnInfo(name = "totalMinutes") val totalMinutes: Int = 0,
    @ColumnInfo(name = "currentState") val currentState: PomodoroState = PomodoroState.IDLE ,
    @ColumnInfo(name = "currentRound") val currentRound: Int = 1
)
