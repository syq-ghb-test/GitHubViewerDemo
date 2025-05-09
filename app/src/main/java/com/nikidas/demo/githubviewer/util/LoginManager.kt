package com.nikidas.demo.githubviewer.util

import android.content.Context
import android.content.SharedPreferences

object LoginManager {
    private const val PREF_NAME = "login_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_AVATAR = "avatarUrl"

    data class User(val username: String, val avatarUrl: String)

    fun saveUser(context: Context, username: String, avatarUrl: String) {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_USERNAME, username).putString(KEY_AVATAR, avatarUrl).apply()
    }

    fun getUser(context: Context): User? {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val username = sp.getString(KEY_USERNAME, null)
        val avatar = sp.getString(KEY_AVATAR, "") ?: ""
        return if (username != null) User(username, avatar) else null
    }

    fun logout(context: Context) {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sp.edit().clear().apply()
    }
} 