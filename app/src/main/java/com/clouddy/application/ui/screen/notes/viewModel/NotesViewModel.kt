package com.clouddy.application.ui.screen.notes.viewModel


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.data.network.local.mapper.toNoteItem
import com.clouddy.application.data.network.local.repository.NotesRepository
import com.clouddy.application.domain.model.NoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.clouddy.application.core.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository) : ViewModel() {

    val notes: StateFlow<List<NoteItem>> = repository.allNotes
        .map { list -> list.map { it.toNoteItem() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val backendOnline = repository.isBackendAvailable()
            if (backendOnline) {
                repository.syncNotesWithServer()
            } else {
                Log.w("NotesViewModel", "Backend offline — usando somente dados locais")
            }
        }
    }

    fun insert(note: Note) = viewModelScope.launch {
        val formattedDate = formatDate(LocalDate.now())
        val noteWithFormattedDate = note.copy(date = formattedDate)
        val newId = repository.insert(noteWithFormattedDate)
        Log.d("INSERT", "Nota inserida com ID: $newId")
    }


    // Métodos auxiliares para manejar NoteItem en el ViewModel


    fun insertOrUpdateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val isConnected = repository.isBackendAvailable()

            if (isConnected) {
                if (note.remoteId == null) {
                    repository.insertNoteRemoteAndLocal(note)
                } else if (note.isUpdated) {
                    repository.updateNoteRemoteAndLocal(note)
                } else {
                    // Nenhuma ação necessária — nota já está sincronizada
                    Log.d("NotesViewModel", "Nota já sincronizada — nenhuma ação necessária.")
                }
            } else {
                val localNote = if (note.remoteId == null) {
                    note.copy(isSynced = false)
                } else {
                    note.copy(isUpdated = true, isSynced = false)
                }

                repository.insert(localNote)
            }
        }
    }

    fun updateNote(note: Note, context: Context) {
        viewModelScope.launch {
            if (note.remoteId == null) {
                repository.insert(note)
                Log.e("ViewModel", "Tentou atualizar nota sem remoteId. Inserindo local.")
            } else {
                repository.updateNoteRemoteAndLocal(note)
            }
        }
    }

    fun deleteNote(note: Note, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val networkUtils = NetworkUtils()
            if (networkUtils.isConnected(context)) {
                repository.deleteNoteRemoteAndLocal(note)
            } else {
                val localNote = note.copy(isDeleted = true, isSynced = false)
                repository.updateNoteRemoteAndLocal(localNote)
            }
        }
    }

    fun syncNotesIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncNotesWithServer()
        }
    }

    fun syncNotesWithServer() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.syncNotesWithServer()
        }
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }


    fun getNotesByDate(date: LocalDate): LiveData<List<Note>> {
        val formattedDate = formatDate(date)
        return repository.getNotesByDate(formattedDate)
    }
}