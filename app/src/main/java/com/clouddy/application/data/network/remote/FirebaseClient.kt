package com.clouddy.application.data.network.remote

import com.clouddy.application.data.model.UserData
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseClient @Inject constructor(private val apiService: ApiService) {

    val auth : FirebaseAuth get() = FirebaseAuth.getInstance()
    suspend fun registerUser(userData: UserData) = apiService.registerUser(userData)
}