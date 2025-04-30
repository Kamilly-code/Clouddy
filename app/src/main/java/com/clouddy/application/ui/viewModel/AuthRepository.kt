package com.clouddy.application.ui.viewModel

import com.clouddy.application.data.network.FirebaseClient
import com.clouddy.application.data.network.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val firebaseClient: FirebaseClient
) {

    fun login(email: String, password: String, onResult: (AuthState) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(AuthState.Error("Correo electrónico y la contraseña son obligatorios"))
            return
        }
        onResult(AuthState.Loading)

        firebaseClient.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(AuthState.Authenticated)
                } else {
                    onResult(AuthState.Error(task.exception?.message ?: "Error desconocido"))
                }
            }
    }

    fun registerWithFirebaseAndApi(
        userData: UserData,
        onResult: (AuthState) -> Unit
    ) {
        if (
            userData.email.isBlank() || userData.password.isBlank() ||
            userData.nombreUser.isBlank() || userData.genero.isBlank()
        ) {
            onResult(AuthState.Error("Todos os campos são obrigatórios"))
            return
        }

        onResult(AuthState.Loading)

        firebaseClient.auth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Se cria en el firebase y se guarda en la API
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = firebaseClient.registerUser(userData)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    onResult(AuthState.Authenticated)
                                } else {
                                    onResult(AuthState.Error("Error al registrarse en la API"))
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                onResult(AuthState.Error("Error: ${e.message}"))
                            }
                        }
                    }
                } else {
                    onResult(AuthState.Error(task.exception?.message ?: "Error desconocido"))
                }
            }
    }

    fun signOut(onResult: () -> Unit) {
        firebaseClient.auth.signOut()
        onResult()
    }

    suspend fun saveUserToApi(userData: UserData, onResult: (AuthState) -> Unit) {
        try {
            firebaseClient.registerUser(userData).let { response ->
                if (response.isSuccessful) {
                    onResult(AuthState.Authenticated)
                } else {
                    onResult(AuthState.Error("Error al guardar los datos del usuario en la API"))
                }
            }
        } catch (e: Exception) {
            onResult(AuthState.Error("Error: ${e.message}"))
        }
    }


    fun isUserAuthenticated(): Boolean {
        return firebaseClient.auth.currentUser != null
    }
}