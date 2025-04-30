package com.clouddy.application.data.network


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService{
    @POST("/users")
    suspend fun registerUser(@Body user: UserData): Response<UserData>
}
