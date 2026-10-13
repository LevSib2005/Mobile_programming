package com.example.mobile_programming.data.local

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveUserId(id: Long) {
        prefs.edit().putLong(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)

    fun isLoggedIn(): Boolean = getUserId() != -1L

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
    }
}