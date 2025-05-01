package com.clouddy.application.data.repository

import com.clouddy.application.data.network.remote.FirebaseClient
import com.clouddy.application.data.model.UserData
import com.clouddy.application.domain.usecase.AuthState
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
            onResult(AuthState.Error("Todos los campos son obligatorios"))
            return
        }

        onResult(AuthState.Loading)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = firebaseClient.registerUser(userData)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        firebaseClient.auth.createUserWithEmailAndPassword(
                            userData.email,
                            userData.password
                        )
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onResult(AuthState.Authenticated)
                                } else {
                                    onResult(
                                        AuthState.Error(
                                            task.exception?.message
                                                ?: "Error al crear el usuario en Firebase"
                                        )
                                    )
                                }
                            }
                    } else {
                        onResult(AuthState.Error("Error al conectarse con la API, verifique si todos los campos son correctos"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(AuthState.Error("Erro: ${e.message}"))
                }
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