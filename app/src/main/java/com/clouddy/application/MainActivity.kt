package com.clouddy.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.clouddy.application.core.navigation.NavigationWrapper
import com.clouddy.application.ui.screen.AddNote
import com.clouddy.application.ui.screen.NotesApp
import com.clouddy.application.ui.screen.login.LoginScreen
import com.example.clouddy.ui.theme.ClouddyTheme
import com.example.clouddy.ui.theme.LoginColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClouddyTheme {
                NotesApp()
            }
        }
    }
}

