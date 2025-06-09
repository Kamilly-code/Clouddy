package com.clouddy.application

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PreferencesManager @Inject constructor(private val context: Context){

    companion object {
        private const val KEY_USER_ID = "firebase_user_id"
        private const val PREFS_NAME = "app_prefs"
    }

    private val _userIdFlow = MutableStateFlow(getUserId())
    val userIdFlow: StateFlow<String?> = _userIdFlow

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserId(userId: String) {
        getPrefs().edit().putString(KEY_USER_ID, userId).apply()
        _userIdFlow.value = userId
    }

    fun getUserId(): String? {
        return getPrefs().getString(KEY_USER_ID, null)
    }

    fun clearUserId() {
        getPrefs().edit().remove(KEY_USER_ID).apply()
        _userIdFlow.value = null
    }

}