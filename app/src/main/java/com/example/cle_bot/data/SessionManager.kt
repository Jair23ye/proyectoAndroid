package com.example.cle_bot.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("clebot_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER = "user_data"
        private const val KEY_LOGIN_TIME = "login_timestamp"
        private const val SESSION_DURATION_DAYS = 15
        private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L
    }

    fun saveSession(user: UserDto) {
        val userJson = gson.toJson(user)
        prefs.edit().apply {
            putString(KEY_USER, userJson)
            putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            apply()
        }
    }

    fun getUser(): UserDto? {
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        
        // Verificar si la sesión ha expirado (15 días)
        if (currentTime - loginTime > SESSION_DURATION_DAYS * MILLIS_IN_DAY) {
            clearSession()
            return null
        }

        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            gson.fromJson(userJson, UserDto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getUser() != null
    }
}
