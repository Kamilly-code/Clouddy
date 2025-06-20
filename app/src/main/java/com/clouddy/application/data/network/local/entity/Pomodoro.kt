package com.clouddy.application.data.network.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pomodoro_table")
data class Pomodoro(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "focusTime") val focusTime: Int,
    @ColumnInfo(name = "shortBreakTime") val shortBreakTime: Int,
    @ColumnInfo(name = "longBreakTime") val longBreakTime: Int,
    @ColumnInfo(name = "rounds") val rounds: Int,
    @ColumnInfo(name = "totalMinutes") val totalMinutes: Int = 0
)
