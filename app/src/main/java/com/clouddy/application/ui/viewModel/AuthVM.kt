package com.clouddy.application.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.network.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthVM @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        _authState.value = if (authRepository.isUserAuthenticated()) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        authRepository.login(email, password) {
            _authState.postValue(it)
        }
    }

    fun register(
        email: String,
        password: String,
        repeatPassword: String,
        name: String,
        genero: String
    ) {
        val userData = UserData(
            nombreUser = name,
            email = email,
            password = password,
            repeatPassword = repeatPassword,
            genero = genero
        )

        authRepository.registerWithFirebaseAndApi(userData) {
            _authState.postValue(it)
        }
    }

    fun signOut() {
        authRepository.signOut {
            _authState.value = AuthState.Unauthenticated
        }
    }
}


