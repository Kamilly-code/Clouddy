package com.clouddy.application.ui.screen.calendar.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.PreferencesManager
import com.clouddy.application.data.network.local.dao.NoteDao
import com.clouddy.application.data.network.local.dao.TaskDao
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.data.network.local.entity.Task
import com.clouddy.application.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    init {
        viewModelScope.launch {
            _currentUserId.value = getUserId()
        }
    }

    private fun getUserId(): String? {
        val userId = preferencesManager.getUserId()
        if (userId.isNullOrEmpty()) {
            val firebaseUser = authRepository.getCurrentUser()
            firebaseUser?.uid?.let { uid ->
                preferencesManager.saveUserId(uid)
                return uid
            }
            return null
        }
        return userId
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun getNotesForDate(date: LocalDate): Flow<List<Note>> {
        val formattedDate = formatDate(date)
        return currentUserId.value?.let { userId ->
            noteDao.getNotesByDate(formattedDate, userId)
        } ?: flowOf(emptyList())
    }

    fun getTasksForDate(date: LocalDate): Flow<List<Task>> {
        val formattedDate = formatDate(date)
        return currentUserId.value?.let { userId ->
            taskDao.getTasksByDate(formattedDate, userId)
        } ?: flowOf(emptyList())
    }

    fun updateTaskCompletion(task: Task) {
        viewModelScope.launch {
            task.id?.let { taskId ->
                taskDao.updateFull(task)
            }
        }
    }
}