package com.example.newsapp.data.local.user

import UserPreferences
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val db = AppDataBase.getDataBase(context)
    private val prefs = UserPreferences(context)

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult

    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> = _registerResult
    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _loginResult.value = false
                return@launch
            }

            val user = db.userDao().login(email.trim(), password.trim())
            if (user != null) {
                prefs.saveLoginState(true)
                _loginResult.value = true
            } else {
                _loginResult.value = false
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                _registerResult.value = false
                return@launch
            }

            val existingUser = db.userDao().getUserByEmail(email.trim())
            if (existingUser != null) {
                _registerResult.value = false
            } else {
                db.userDao().insertUser(
                    User(
                        userName = username.trim(),
                        email = email.trim(),
                        password = password.trim()
                    )
                )
                _registerResult.value = true
            }
        }
    }
}