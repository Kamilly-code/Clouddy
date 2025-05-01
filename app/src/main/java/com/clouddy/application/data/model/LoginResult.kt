package com.clouddy.application.data.model

sealed class LoginResult {
    object Error : LoginResult()
    data class Success(val verified : Boolean) : LoginResult()
}