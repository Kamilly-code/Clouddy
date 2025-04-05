package com.clouddy.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.clouddy.application.ui.screen.AddNote
import com.clouddy.application.ui.screen.NotesApp
import com.example.clouddy.ui.theme.ClouddyTheme

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

