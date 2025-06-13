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

    private var cachedToken: String? = null
    private var tokenExpiration: Long = 0

    private val coroutineScope = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
        Log.e("AuthRepository", "Coroutine error", e)
    })

    fun getCurrentUser(): FirebaseUser? {
        return firebaseClient.auth.currentUser
    }

    suspend fun getValidToken(): String? {
        return try {
            val now = System.currentTimeMillis()
            if (cachedToken != null && now < tokenExpiration) {
                return cachedToken
            }

            val firebaseUser = firebaseClient.auth.currentUser
            val tokenResult = firebaseUser?.getIdToken(true)?.await()

            tokenResult?.let {
                cachedToken = "Bearer ${it.token}"
                tokenExpiration = now + (it.expirationTimestamp - it.authTimestamp) * 1000 - 30000 // 30s de margem
            }

            cachedToken
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get token", e)
            null
        }
    }

    fun registerUser(userData: UserData, onResult: (AuthState) -> Unit
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
                    val firebaseUser = task.result?.user ?: run {
                        onResult(AuthState.Error("Firebase user is null"))
                        return@addOnCompleteListener
                    }

                    // Forçar atualização do token
                    firebaseUser.getIdToken(true).addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val token = "Bearer ${tokenTask.result?.token ?: run {
                                firebaseUser.delete()
                                onResult(AuthState.Error("Token is null"))
                                return@addOnCompleteListener
                            }}"

                            // Log para debug
                            Log.d("AuthRepository", "Firebase UID: ${firebaseUser.uid}")
                            Log.d("AuthRepository", "Token: ${token.take(20)}...")

                            apiService.syncUser(token, FirebaseUserSyncRequest(
                                nombreUser = userData.nombreUser,
                                email = userData.email,
                                genero = userData.genero
                            )).enqueue(object : Callback<UserResponseDTO> {
                                override fun onResponse(call: Call<UserResponseDTO>, response: Response<UserResponseDTO>) {
                                    if (response.isSuccessful) {
                                        preferencesManager.saveUserId(firebaseUser.uid)
                                        onResult(AuthState.Authenticated)
                                    } else {
                                        firebaseUser.delete()
                                        val errorBody = response.errorBody()?.string() ?: "No error body"
                                        Log.e("AuthRepository", "Sync failed: ${response.code()} - $errorBody")
                                        onResult(AuthState.Error("Sync failed: ${response.code()}"))
                                    }
                                }

                                override fun onFailure(call: Call<UserResponseDTO>, t: Throwable) {
                                    firebaseUser.delete()
                                    Log.e("AuthRepository", "Network error", t)
                                    onResult(AuthState.Error("Network error: ${t.message}"))
                                }
                            })
                        } else {
                            firebaseUser.delete()
                            Log.e("AuthRepository", "Token error", tokenTask.exception)
                            onResult(AuthState.Error("Token error: ${tokenTask.exception?.message}"))
                        }
                    }
                } else {
                    onResult(AuthState.Error(task.exception?.message ?: "Firebase error"))
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
