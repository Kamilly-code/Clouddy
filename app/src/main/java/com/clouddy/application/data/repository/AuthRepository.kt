package com.clouddy.application.data.repository

import android.content.Context
import android.util.Log
import com.clouddy.application.PreferencesManager
import com.clouddy.application.data.network.remote.user.firebase.FirebaseClient
import com.clouddy.application.data.model.UserData
import com.clouddy.application.data.network.remote.user.ApiService
import com.clouddy.application.data.network.remote.user.firebase.FirebaseUserSyncRequest
import com.clouddy.application.data.network.remote.user.firebase.UserResponseDTO
import com.clouddy.application.domain.usecase.AuthState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
        Log.e("AuthRepository", "Coroutine error", e)
    })

    fun getCurrentUser(): FirebaseUser? {
        return firebaseClient.auth.currentUser
    }


    fun registerUser(
        userData: UserData,
        onResult: (AuthState) -> Unit
    ) {
        // Validações
        if (userData.email.isBlank() || userData.password.isBlank() ||
            userData.nombreUser.isBlank() || userData.genero.isBlank()) {
            onResult(AuthState.Error("Todos los campos son obligatorios"))
            return
        }

        if (userData.password != userData.repeatPassword) {
            onResult(AuthState.Error("Las contraseñas no coinciden"))
            return
        }

        onResult(AuthState.Loading)

        // 1. Registrar no Firebase
        firebaseClient.auth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user!!
                    val userId = firebaseUser.uid // 🔑 ID crítico para salvar localmente

                    // Obter token JWT
                    firebaseUser.getIdToken(false).addOnCompleteListener { tokenTask ->
                        val idToken = "Bearer ${tokenTask.result?.token}"
                        Log.d("FIREBASE_TOKEN", "Token: $idToken")

                        // Sincronizar com Spring Boot
                        apiService.syncUser(idToken, FirebaseUserSyncRequest(nombreUser = userData.nombreUser,
                            email = userData.email,
                            genero = userData.genero))
                        .enqueue(object : Callback<UserResponseDTO> {
                        override fun onResponse(call: Call<UserResponseDTO>, response: Response<UserResponseDTO>) {
                            Log.d("SYNC_RESPONSE", "Code: ${response.code()}, Message: ${response.message()}") // ✅ Log da resposta
                            if (response.isSuccessful) {
                                // ✅ Armazene o userId do Firebase localmente
                                preferencesManager.saveUserId(userId)
                                onResult(AuthState.Authenticated)
                            } else {
                                Log.e("SYNC_ERROR", "Body: ${response.errorBody()?.string()}")
                                firebaseUser.delete()
                                onResult(AuthState.Error("Sync failed: ${response.code()}"))
                            }
                        }
                            override fun onFailure(call: Call<UserResponseDTO>, t: Throwable) {
                                Log.e("SYNC_FAILURE", "Exception: ${t.message}") // ✅ Log de falha na requisição
                                firebaseUser.delete()
                                onResult(AuthState.Error("Sync error: ${t.message}"))
                            }
                        })
                    }
                } else {
                    onResult(AuthState.Error(task.exception?.message ?: "Error en Firebase"))
                }
            }
    }


    // --- LOGIN ---
    fun login(email: String, password: String, onResult: (AuthState) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(AuthState.Error("Correo y contraseña obligatorios"))
            return
        }

        onResult(AuthState.Loading)

        firebaseClient.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(AuthState.Authenticated)
                } else {
                    onResult(AuthState.Error(task.exception?.message ?: "Error en Firebase"))
                }
            }
    }

    // --- LOGOUT ---
    fun signOut(onResult: () -> Unit) {
        firebaseClient.auth.signOut()
        preferencesManager.clearUserId()
        onResult()
    }

    // --- VERIFICAÇÃO DE AUTENTICAÇÃO ---
    fun isUserAuthenticated(): Boolean {
        return firebaseClient.auth.currentUser != null
    }

    // --- OBTER UID DO USUÁRIO ---
    fun getCurrentUserId(): String? {
        return firebaseClient.auth.currentUser?.uid
    }

    // --- VERIFICAÇÃO DE EMAIL (OPCIONAL) ---
    suspend fun sendVerificationEmail(): Boolean {
        return firebaseClient.auth.currentUser?.sendEmailVerification()?.await() != null
    }

    suspend fun isEmailVerified(): Boolean {
        firebaseClient.auth.currentUser?.reload()?.await()
        return firebaseClient.auth.currentUser?.isEmailVerified ?: false
    }

}
