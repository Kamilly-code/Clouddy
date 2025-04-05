package com.clouddy.application.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.clouddy.application.NoteItem
import com.clouddy.application.database.NoteDataBase
import com.clouddy.application.database.NotesRepository
import com.clouddy.application.database.entity.Note
import com.clouddy.application.mapper.toNoteItem
import kotlinx.coroutines.launch

class NotesViewModel (application: Application) : AndroidViewModel(application) {
    private val noteDao = NoteDataBase.getDataBase(application).getNoteDao()
    private val repository = NotesRepository(noteDao)
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