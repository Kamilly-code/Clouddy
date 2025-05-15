package com.clouddy.application.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.clouddy.application.ui.screen.calendar.FullScreenCalendar
import com.example.clouddy.ui.theme.ClouddyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClouddyTheme {
               // NavigationWrapper()
               // PomodoroScreen()
               //PomodoroList(navController = rememberNavController())
                FullScreenCalendar( selectedDate = null, onDateSelected = {})

            }
        }
    }
}