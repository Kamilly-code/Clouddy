package com.clouddy.application.data.network.remote.task

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApiService {
    @POST("tareas")
    suspend fun insertTask(@Body task: TaskRequestDto): Response<TaskModelResponse>

    @PUT("tareas/{id}")
    suspend fun updateTaskStatus(
        @Path("id") id: Long,
        @Query("isCompleted") isCompleted: Boolean
    ): Response<TaskModelResponse>

    @DELETE("tareas/{id}")
    suspend fun deleteTask(@Path("id") id: Long): Response<Unit>
}