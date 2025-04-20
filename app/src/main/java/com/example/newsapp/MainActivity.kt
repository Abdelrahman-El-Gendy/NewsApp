package com.example.newsapp

import AppNavigation
import UserPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val userPrefs = remember { UserPreferences(context) }
            val isLoggedIn by userPrefs.isLoggedIn.collectAsState(initial = false)

           // Text("Login State: ${if (isLoggedIn) "Logged In" else "Logged Out"}")

            AppNavigation(startDestination = if (isLoggedIn) "main" else "login")
        }
    }
}


