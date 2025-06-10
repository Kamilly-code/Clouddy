package com.clouddy.application.data.network.remote.task

import com.clouddy.application.domain.model.TaskItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskApiService {
    @POST("tareas")
    suspend fun insertTask(@Body task: TaskRequestDto,
                           @Header("Authorization") token: String): Response<TaskItem>

    @PUT("tareas/{id}")
    suspend fun updateTaskStatus(
        @Path("id") id: String,
        @Query("isCompleted") isCompleted: Boolean,
        @Body taskRequestDTO: TaskRequestDto,
        @Header("Authorization") token: String
    ): Response<TaskItem>

    @DELETE("tareas/{id}")
    suspend fun deleteTask(@Path("id") id: String,
                           @Header("Authorization") token: String): Response<Void>

}