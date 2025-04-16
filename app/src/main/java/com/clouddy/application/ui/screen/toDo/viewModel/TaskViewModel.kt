package com.clouddy.application.ui.screen.toDo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.local.entity.Task
import com.clouddy.application.data.local.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository : TaskRepository) : ViewModel(){
    val tasks = repository.tasks.asLiveData()

    fun addTask(name: String){
        viewModelScope.launch {
            repository.insert(Task(task = name))
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

}