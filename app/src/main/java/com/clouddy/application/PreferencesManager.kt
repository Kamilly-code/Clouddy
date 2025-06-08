package com.clouddy.application

import android.content.Context
import android.content.SharedPreferences
import com.clouddy.application.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.apply

class PreferencesManager @Inject constructor(private val context: Context){

    companion object {
        private const val KEY_USER_ID = "firebase_user_id"
        private const val PREFS_NAME = "app_prefs"
    }

    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserId(userId: String) {
        getPrefs().edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return getPrefs().getString(KEY_USER_ID, null)
    }
}