package com.clouddy.application.data.network.remote.pomodoro

import com.clouddy.application.data.network.local.entity.Pomodoro
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PomodoroApiService {
    @GET("/pomodoros")
    suspend fun getPomodoroSettings(
        @Header("Authorization") token: String): Response<PomodoroResponseDto>

    @POST("/pomodoros")
    suspend fun insertPomodoro(@Body request: PomodoroRequestDto,
                               @Header("Authorization") token: String): PomodoroResponseDto

    @PUT("/pomodoros/{id}")
    suspend fun updatePomodoro(
        @Path("id") id: Long,
        @Body request: PomodoroRequestDto,
        @Header("Authorization") token: String
    ): PomodoroResponseDto

    @DELETE("/pomodoros")
    suspend fun deleteAllPomodoros(
        @Header("Authorization") token: String): String

    @GET("/pomodoros/ping")
    suspend fun ping(): String
}