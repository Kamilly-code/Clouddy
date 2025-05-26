package com.clouddy.application.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.clouddy.application.core.navigation.NavigationWrapper
import com.example.clouddy.ui.theme.ClouddyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClouddyTheme {
                val navController = rememberNavController()
                NavigationWrapper()
               // PomodoroScreen()
               //PomodoroList(navController = rememberNavController())
               // FullScreenCalendar( onDateSelected = {}, navigateToDayScreen = {navController.navigate(
               //     DayScreen)})

            }
        }
    }
}