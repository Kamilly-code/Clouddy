package com.clouddy.application.ui.screen.pomodoro.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clouddy.application.ui.screen.pomodoro.components.TimerCard
import com.clouddy.application.ui.screen.pomodoro.viewModel.PomodoroViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroList(navigateToPomodoroScreen: (() -> Unit)? = null) {
    val viewModel: PomodoroViewModel = hiltViewModel()

    val settings by viewModel.pomodoroSettings.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // entidades do room
    var focusTime by remember(settings) { mutableFloatStateOf(settings?.focusTime?.toFloat() ?: 25f) }
    var shortBreak by remember(settings) { mutableFloatStateOf(settings?.shortBreakTime?.toFloat() ?: 25f) }
    var longBreak by remember(settings) { mutableFloatStateOf(settings?.longBreakTime?.toFloat() ?: 25f) }
    var rounds by remember(settings) { mutableFloatStateOf(settings?.rounds?.toFloat() ?: 25f) }

    ClouddyTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            TimerCard(
                title = "Focus",
                value = focusTime,
                onValueChange = { focusTime = it }
            )
            TimerCard(
                title = "Short break",
                value = shortBreak,
                onValueChange = { shortBreak = it }
            )
            TimerCard(
                title = "Long break",
                value = longBreak,
                onValueChange = { longBreak = it }
            )
            TimerCard(
                title = "Rounds",
                value = rounds,
                onValueChange = { rounds = it },
                valueRange = 1f..10f,
                unit = "rounds"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.resetPomodoroSettings()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text("Reset", color = Color.White, fontSize = 18.sp)
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.savePomodoroSettingsAndWait(
                                focusTime = focusTime.toInt(),
                                shortBreakTime = shortBreak.toInt(),
                                longBreakTime = longBreak.toInt(),
                                rounds = rounds.toInt()
                            ) {
                                navigateToPomodoroScreen?.invoke() // Navega após salvar
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text("OK", color = Color.White, fontSize = 18.sp)
                }}
        }
    }
}