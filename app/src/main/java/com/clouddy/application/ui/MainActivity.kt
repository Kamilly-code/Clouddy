package com.clouddy.application.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.clouddy.application.core.navigation.NavigationWrapper
import com.clouddy.application.ui.screen.home.HomeScreen
import com.clouddy.application.ui.screen.login.viewModel.AuthVM
import com.clouddy.application.ui.screen.notes.NotesApp
import com.clouddy.application.ui.screen.toDo.screen.TaskScreen
import com.example.clouddy.ui.theme.ClouddyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClouddyTheme {
                NavigationWrapper()
            }
        }
    }
}