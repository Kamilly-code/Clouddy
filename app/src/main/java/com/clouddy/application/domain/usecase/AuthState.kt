package com.clouddy.application.domain.usecase

import com.clouddy.application.data.network.remote.user.firebase.UserResponseDTO

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    data class SyncSuccess(val user: UserResponseDTO) : AuthState()  // Novo estado
}