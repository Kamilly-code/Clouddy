package com.clouddy.application.ui.screen.calendar.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.network.local.dao.NoteDao
import com.clouddy.application.data.network.local.dao.TaskDao
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.data.network.local.entity.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val taskDao: TaskDao
) : ViewModel() {

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun getNotesForDate(date: LocalDate):Flow<List<Note>> {
        val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        Log.d("CalendarViewModel", "Buscando notas para data: $formattedDate")
        return noteDao.getNotesByDate(formattedDate)
    }

    fun getTasksForDate(date: LocalDate): LiveData<List<Task>> {
        val formattedDate = formatDate(date)
        return taskDao.getTasksByDate(formattedDate)
    }

    fun updateTaskCompletion(task: Task) {
        viewModelScope.launch {
            task.id?.let { taskId ->
                taskDao.update(taskId, !task.isCompleted)
            }
        }
    }
}