package com.clouddy.application.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Registro

@Serializable
object Home

@Serializable
object Notes

@Serializable
object Task

@Serializable
object Pomodoro

@Serializable
object PomodoroScreen

@Serializable
data class DayScreenArgs(val selectedDate: String)

@Serializable
object Calendar