package com.nikidas.demo.githubviewer.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nikidas.demo.githubviewer.util.LoginManager

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _loginState = MutableLiveData<LoginManager.User?>()
    val loginState: LiveData<LoginManager.User?> = _loginState

    companion object {
        private const val TAG = "LoginViewModel"
    }

    init {
        Log.d(TAG, "Initializing LoginViewModel")
        checkLoginState()
    }

    fun checkLoginState() {
        Log.d(TAG, "Checking login state")
        val user = LoginManager.getUser(getApplication())
        Log.d(TAG, "Current user: $user")
        _loginState.value = user
    }

    fun logout() {
        Log.d(TAG, "Logging out")
        LoginManager.logout(getApplication())
        _loginState.value = null
    }
} 