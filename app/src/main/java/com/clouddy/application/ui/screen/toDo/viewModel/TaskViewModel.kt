package com.clouddy.application.ui.screen.toDo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.local.entity.Task
import com.clouddy.application.data.local.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.text.insert

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository : TaskRepository) : ViewModel(){
    val tasks = repository.tasks.asLiveData()

    fun addTask(name: String){
        viewModelScope.launch {
            val formattedDate = formatDate(LocalDate.now())
            repository.insert(Task(task = name, date = formattedDate))
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun storeTaskCompletation ( task: Task ) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            repository.update(updatedTask)
        }
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun getTasksByDate(date: LocalDate): LiveData<List<Task>> {
        val formattedDate = formatDate(date)
        return repository.getTasksByDate(formattedDate)
    }



}