package com.clouddy.application.data.network.remote.user

import com.clouddy.application.data.model.UserData
import com.clouddy.application.data.network.remote.note.NotesApiService
import com.clouddy.application.data.network.remote.user.firebase.FirebaseUserSyncRequest
import com.clouddy.application.data.network.remote.user.firebase.UserResponseDTO
import com.clouddy.application.di.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService{
    @POST("/users/sync")
    fun syncUser(
        @Header("Authorization") token: String,
        @Body request: FirebaseUserSyncRequest
    ): Call<UserResponseDTO>

}