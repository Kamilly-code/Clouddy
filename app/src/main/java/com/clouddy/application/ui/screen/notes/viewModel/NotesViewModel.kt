package com.clouddy.application.ui.screen.notes.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.local.db.NoteDataBase
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.data.local.mapper.toNoteItem
import com.clouddy.application.data.local.repository.NotesRepository
import com.clouddy.application.domain.model.NoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository) : ViewModel() {

    val notes: LiveData<List<NoteItem>> = repository.allNotes.map { list ->
        list.map { it.toNoteItem() }
    }

    val allNotes: LiveData<List<Note>> = repository.allNotes

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }



}