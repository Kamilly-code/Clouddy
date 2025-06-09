package com.clouddy.application.ui.screen.pomodoro.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.PreferencesManager
import com.clouddy.application.R
import com.clouddy.application.ui.screen.pomodoro.components.MoonProgress
import com.clouddy.application.ui.screen.pomodoro.viewModel.PomodoroViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import kotlinx.coroutines.delay


@Composable
fun PomodoroScreen() {
    val viewModel: PomodoroViewModel = hiltViewModel()
    val settings by viewModel.pomodoroSettings.collectAsState()
    val currentRound by viewModel.currentRound.collectAsState()
    val isCycleFinished by viewModel.isCycleFinished.collectAsState()
    val shouldStopTimer by viewModel.shouldStopTimer.collectAsState()

    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val currentUserId = preferencesManager.getUserId()


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

    settings?.let { safeSettings ->
        val focusTime = (settings!!.focusTime) * 60
        val shortBreak = (settings!!.shortBreakTime) * 60
        val longBreak = (settings!!.longBreakTime) * 60
        val totalRounds = settings!!.rounds

        var timeLeft by remember { mutableIntStateOf(focusTime) }

        var phase by remember { mutableStateOf("Focus") }

        // Timer
        LaunchedEffect(isRunning, timeLeft, shouldStopTimer) {
            if (isRunning && !shouldStopTimer) {
                if (timeLeft > 0) {
                    delay(1000L)
                    timeLeft -= 1
                } else {
                    when (phase) {
                        "Focus" -> {
                            viewModel.onPomodoroCompleted(false)
                            phase =
                                if ((currentRound + 1) >= totalRounds) "Long Break" else "Short Break"
                            timeLeft = if (phase == "Long Break") longBreak else shortBreak
                        }

                        "Short Break", "Long Break" -> {
                            viewModel.onPomodoroCompleted(true)
                            if (!isCycleFinished) {
                                phase = "Focus"
                                timeLeft = focusTime
                            } else {
                                isRunning = false
                            }
                        }
                    }
                }
            } else if (shouldStopTimer) {
                isRunning = false
            }
        }

        // Efeito para resetar quando o ciclo terminar
        LaunchedEffect(isCycleFinished) {
            if (isCycleFinished) {
                isRunning = false
            }
        }


        ClouddyTheme {
            Scaffold { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2677B0),
                                    Color(0xFF10324A)
                                )
                            )
                        )
                        .padding(paddingValues)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.full_nube),
                        contentDescription = "Imagem decorativa",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color(0xFF00042A).copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Text(text = "${currentRound}/$totalRounds", color = Color.White)
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
                            onClick = {
                                if (isCycleFinished) return@IconButton // Não faz nada se o ciclo estiver completo
                                if (!isRunning) viewModel.onPlayPressed()
                                isRunning = !isRunning
                            },
                            modifier = Modifier.size(48.dp),
                            enabled = !isCycleFinished // Desabilita o botão quando o ciclo estiver completo
                        ) {
                            if (isRunning) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pause),
                                    contentDescription = "Pause",
                                    tint = if (isCycleFinished) Color.Gray else Color.White // Opcional: muda a cor quando desabilitado
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Start",
                                    tint = if (isCycleFinished) Color.Gray else Color.White // Opcional: muda a cor quando desabilitado
                                )
                        }
                        }
                        if (isCycleFinished) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Ciclo completo! 🎉", color = Color.Green)
                            }
                        }
                    }
                }
            }
        }
    }}