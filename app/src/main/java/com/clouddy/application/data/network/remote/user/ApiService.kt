package com.clouddy.application.data.network.remote.user

import com.clouddy.application.data.model.UserData
import com.clouddy.application.data.network.remote.note.NotesApiService
import com.clouddy.application.di.RetrofitClient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService{
    @POST("/users")
    suspend fun registerUser(@Body user: UserData): Response<UserData>

    val api: NotesApiService
        get() = RetrofitClient.getRetrofitClient().create(NotesApiService::class.java)

}