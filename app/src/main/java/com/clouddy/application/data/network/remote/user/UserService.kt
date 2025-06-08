package com.clouddy.application.data.network.remote.user

import com.clouddy.application.data.network.remote.user.firebase.FirebaseClient
import javax.inject.Inject

class UserService @Inject constructor(private  val firebase: FirebaseClient) {
}