package com.clouddy.application.data.network.local.mapper

import com.clouddy.application.core.utils.pomodoro.PomodoroState
import com.clouddy.application.data.network.local.entity.Pomodoro
import com.clouddy.application.data.network.remote.pomodoro.PomodoroRequestDto
import com.clouddy.application.data.network.remote.pomodoro.PomodoroResponseDto

object PomodoroMapper {

    fun toRequest(entity: Pomodoro): PomodoroRequestDto {
        return PomodoroRequestDto(
            focusTime = entity.focusTime,
            shortBreakTime = entity.shortBreakTime,
            longBreakTime = entity.longBreakTime,
            rounds = entity.rounds,
            totalMinutes = entity.totalMinutes,
            currentState = entity.currentState.name
        )
    }

    fun fromResponse(dto: PomodoroResponseDto): Pomodoro {
        val state = try {
            PomodoroState.valueOf(dto.currentState ?: PomodoroState.IDLE.name)
        } catch (e: IllegalArgumentException) {
            PomodoroState.IDLE
        }
        return Pomodoro(
            id = dto.id,
            focusTime = dto.focusTime,
            shortBreakTime = dto.shortBreakTime,
            longBreakTime = dto.longBreakTime,
            rounds = dto.rounds,
            totalMinutes = dto.totalMinutes,
            currentState = state
        )
    }
}
