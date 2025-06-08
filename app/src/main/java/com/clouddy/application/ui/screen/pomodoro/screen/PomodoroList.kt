package com.clouddy.application.ui.screen.pomodoro.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    val userId = viewModel.getUserId()
    if (userId == null) {
        Log.e("PomodoroList", "userId está nulo. Não será exibido conteúdo.")
        return
    }
    val settings by viewModel.pomodoroSettings.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // entidades do room
    var focusTime by remember(settings) { mutableFloatStateOf(settings?.focusTime?.toFloat() ?: 25f) }
    var shortBreak by remember(settings) { mutableFloatStateOf(settings?.shortBreakTime?.toFloat() ?: 5f) }
    var longBreak by remember(settings) { mutableFloatStateOf(settings?.longBreakTime?.toFloat() ?: 15f) }
    var rounds by remember(settings) { mutableFloatStateOf(settings?.rounds?.toFloat() ?: 4f) }

    ClouddyTheme {
        Scaffold(content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF2677B0),
                                Color(0xFF10324A)
                            )
                        )
                    )
                    .padding(paddingValues)
            ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            TimerCard(
                title = "Focus",
                value = focusTime,
                onValueChange = { focusTime = it },
                modifier = Modifier.weight(1f)
            )
            TimerCard(
                title = "Short break",
                value = shortBreak,
                onValueChange = { shortBreak = it },
                modifier = Modifier.weight(1f)
            )
            TimerCard(
                title = "Long break",
                value = longBreak,
                onValueChange = { longBreak = it },
                modifier = Modifier.weight(1f)
            )
            TimerCard(
                title = "Rounds",
                value = rounds,
                onValueChange = { rounds = it },
                modifier = Modifier.weight(1f),
                valueRange = 1f..10f,
                unit = "rounds"
            )

            Spacer(modifier = Modifier.height(1.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.resetPomodoroSettings(userId = userId)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text("RESETAR", color = Color.White, fontSize = 18.sp)
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
                                navigateToPomodoroScreen?.let { it() } ?:
                                Log.e("Navigation", "Callback de navegação é null")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
                ) {
                    Text("SALVAR", color = Color.White, fontSize = 18.sp)
                }
            }
        }
        }
    }
)
}
}