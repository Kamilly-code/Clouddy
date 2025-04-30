package com.clouddy.application.data.network.response

sealed class LoginResult {
    object Error : LoginResult()
    data class Success(val verified : Boolean) : LoginResult()
}