package com.clouddy.application.data.network.local.mapper

import com.clouddy.application.core.utils.pomodoro.PomodoroState
import com.clouddy.application.data.network.local.entity.Pomodoro
import com.clouddy.application.data.network.remote.pomodoro.PomodoroRequestDto
import com.clouddy.application.data.network.remote.pomodoro.PomodoroResponseDto

object PomodoroMapper {
    fun toRequest(pomodoro: Pomodoro): PomodoroRequestDto {
        return PomodoroRequestDto(
            focusTime = pomodoro.focusTime,
            shortBreakTime = pomodoro.shortBreakTime,
            longBreakTime = pomodoro.longBreakTime,
            rounds = pomodoro.rounds,
            totalMinutes = pomodoro.totalMinutes,
            currentState = pomodoro.currentState.name,
            currentRound = pomodoro.currentRound,
            lastUpdatedDate = pomodoro.lastUpdatedDate.toString(),
            userId = pomodoro.userId
        )
    }

    fun fromResponse(dto: PomodoroResponseDto): Pomodoro {
        return Pomodoro(
            id = dto.id,
            focusTime = dto.focusTime,
            shortBreakTime = dto.shortBreakTime,
            longBreakTime = dto.longBreakTime,
            rounds = dto.rounds,
            totalMinutes = dto.totalMinutes,
            currentState = PomodoroState.valueOf(dto.currentState),
            currentRound = dto.currentRound,
            lastUpdatedDate = dto.lastUpdatedDate,
            userId = dto.userId
        )
    }}