package com.clouddy.application.ui.screen.calendar.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clouddy.application.ui.screen.calendar.viewModel.CalendarViewModel
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clouddy.application.ui.screen.pomodoro.viewModel.PomodoroViewModel
import com.example.clouddy.ui.theme.ClouddyTheme
import java.time.format.DateTimeFormatter

@Composable
fun DayScreen(){

    val calendarViewModel: CalendarViewModel = hiltViewModel()

    val selectedDate = remember { LocalDate.now() }
    val selectedDateStr = selectedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

    val notes by calendarViewModel.getNotesForDate(selectedDate).observeAsState(emptyList())
    val tasks by calendarViewModel.getTasksForDate(selectedDate).observeAsState(emptyList())

    println("Notas carregadas: $notes")
    println("Tarefas carregadas: $tasks")

    ClouddyTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Lembretes de ${selectedDate.dayOfMonth}/${selectedDate.monthValue}/${selectedDate.year}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Seção de Notas
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
                                text = note.note?: "Sem conteúdo",
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
            Log.d("DayScreen", "Notas recebidas do ViewModel: $notes (quantidade = ${notes.size})")
            Spacer(modifier = Modifier.height(24.dp))

            // Seção de Tarefas
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

            // Caso não haja notas nem tarefas
            if (notes.isEmpty() && tasks.isEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Nenhuma anotação ou tarefa para este dia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
