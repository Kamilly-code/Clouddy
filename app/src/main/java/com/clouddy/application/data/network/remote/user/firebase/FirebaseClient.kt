package com.clouddy.application.data.network.remote.user.firebase

import com.clouddy.application.data.model.UserData
import com.clouddy.application.data.network.remote.user.ApiService
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseClient @Inject constructor(private val apiService: ApiService) {
    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
}