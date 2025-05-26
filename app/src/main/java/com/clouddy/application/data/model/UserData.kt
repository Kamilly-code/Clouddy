package com.clouddy.application.data.model

data class UserData(
    val nombreUser: String,
    val email: String,
    val password: String,
    val repeatPassword: String,
    val genero: String
)