package com.clouddy.application.ui.screen.pomodoro.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.R
import com.clouddy.application.ui.screen.pomodoro.components.MoonProgress
import com.clouddy.application.ui.screen.pomodoro.viewModel.PomodoroViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import kotlinx.coroutines.delay


@Composable
fun PomodoroScreen() {
    val viewModel: PomodoroViewModel = hiltViewModel()
    val settings by viewModel.pomodoroSettings.collectAsState()
    if (settings == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Carregando configurações...")
        }
        return
    }

    var isRunning by remember { mutableStateOf(false) }

    val focusTime = (settings!!.focusTime) * 60
    val shortBreak = (settings!!.shortBreakTime) * 60
    val longBreak = (settings!!.longBreakTime) * 60
    val totalRounds = settings!!.rounds

    var timeLeft by remember { mutableIntStateOf(focusTime) }
    var currentRound by remember { mutableIntStateOf(1) }
    var phase by remember { mutableStateOf("Focus") }

    // Timer
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning) {
            if (timeLeft > 0) {
                delay(1000L)
                timeLeft -= 1
            } else {
                when (phase) {
                    "Focus" -> {
                        phase = if (currentRound % totalRounds == 0) "Long Break" else "Short Break"
                        timeLeft = if (phase == "Long Break") longBreak else shortBreak
                    }
                    "Short Break", "Long Break" -> {
                        currentRound++
                        if (currentRound <= totalRounds) {
                            phase = "Focus"
                            timeLeft = focusTime
                        } else {
                            isRunning = false
                        }
                    }
                }
            }
        }
    }

    ClouddyTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "$currentRound/$totalRounds")
                Spacer(modifier = Modifier.height(20.dp))

                Box(contentAlignment = Alignment.Center) {
                    MoonProgress(
                        timeRemaining = timeLeft,
                        totalTime = when (phase) {
                            "Focus" -> focusTime
                            "Short Break" -> shortBreak
                            "Long Break" -> longBreak
                            else -> focusTime
                        }
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val minutes = (timeLeft / 60)
                        val seconds = (timeLeft % 60)
                        val timeText = String.format("%02d:%02d", minutes, seconds)

                        Text(text = timeText, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = phase, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                IconButton(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.size(48.dp)
                ) {
                    if (isRunning) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pause),
                            contentDescription = "Pause",
                            tint = Color.Black
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Start",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}