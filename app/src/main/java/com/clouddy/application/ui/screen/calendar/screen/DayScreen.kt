package com.clouddy.application.ui.screen.calendar.screen

import android.R.attr.color
import android.R.color
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clouddy.application.ui.screen.calendar.viewModel.CalendarViewModel
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clouddy.application.R
import com.clouddy.application.ui.screen.pomodoro.viewModel.PomodoroViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.format.DateTimeFormatter
import kotlin.text.toLong
import kotlin.toString

@Composable
fun DayScreen() {
    val calendarViewModel: CalendarViewModel = hiltViewModel()

    var currentWeekStart by remember { mutableStateOf(LocalDate.now().with(java.time.DayOfWeek.MONDAY)) }
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }

    // Observar as notas e tarefas com base no selectedDate
    val notes by calendarViewModel.getNotesForDate(selectedDate.value).collectAsState(emptyList())
    val tasks by calendarViewModel.getTasksForDate(selectedDate.value).collectAsState(emptyList())

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF7DB6E1), // Cor 5
            Color(0xFF6BB8FF), // Cor 4
            Color(0xFF4CA6F0), // Cor 3
            Color(0xFF3A8FD0), // Cor 2
            Color(0xFF2677B0), // Cor 1
        )
    )
    val systemUiController = rememberSystemUiController()

    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = false // ou true, se quiser ícones escuros
    )

    systemUiController.setNavigationBarColor(
        color = Color.Transparent,
        darkIcons = false
    )

    ClouddyTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
        )
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize(),
            content = { paddingValues ->
                    // Conteúdo principal
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        // Barra de dias da semana
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            items(7) { index ->
                                val day = currentWeekStart.plusDays(index.toLong())
                                val isSelected = day == selectedDate.value

                                Column(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .size(50.dp)
                                        .background(
                                            if (isSelected) Color(0xFF002F50) else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedDate.value = day },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = day.dayOfWeek.name.take(3),
                                        color = if (isSelected) Color.White else Color.Black,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = day.dayOfMonth.toString(),
                                        color = if (isSelected) Color.White else Color.Black,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botões para mudar de semana
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = {
                                currentWeekStart = currentWeekStart.minusWeeks(1)
                            }) {
                                Text("Semana Anterior")
                            }
                            TextButton(onClick = {
                                currentWeekStart = currentWeekStart.plusWeeks(1)
                            }) {
                                Text("Próxima Semana")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Conteúdo de notas e tarefas
                        Text(
                            text = "Lembretes de ${selectedDate.value.dayOfMonth}/${selectedDate.value.monthValue}/${selectedDate.value.year}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(16.dp))
                                .padding(8.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                if (notes.isNotEmpty()) {
                                    Text(text = "📝 Notas", style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    notes.forEach { note ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            elevation = CardDefaults.cardElevation(4.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(
                                                    text = note.title ?: "Sem título",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = note.note ?: "Sem conteúdo",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Nenhuma nota para este dia.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                if (tasks.isNotEmpty()) {
                                    Text(text = "✅ Tarefas", style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    tasks.forEach { task ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = task.isCompleted,
                                                onCheckedChange = { /* TODO: atualizar no banco */ }
                                            )
                                            Text(text = task.task)
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Nenhuma tarefa para este dia.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
            }
        )
    }
}