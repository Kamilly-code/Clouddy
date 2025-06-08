package com.clouddy.application.ui.screen.notes.viewModel


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clouddy.application.PreferencesManager
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.data.network.local.mapper.toNoteItem
import com.clouddy.application.data.network.local.repository.NotesRepository
import com.clouddy.application.domain.model.NoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.clouddy.application.core.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.text.format
import kotlin.text.insert

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository,
                                         private val preferencesManager: PreferencesManager) : ViewModel() {

    private val _isAuthenticated = kotlinx.coroutines.flow.MutableStateFlow(preferencesManager.getUserId() != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val userId = preferencesManager.getUserId() ?: throw IllegalStateException("User not authenticated")

    val notes: StateFlow<List<NoteItem>> = repository.getAllNotes()
        .map { list -> list.map { it.toNoteItem() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        if (userId != null) {
            loadNotes()
        }
    }

    fun loadNotes() {
        userId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                val backendOnline = repository.isBackendAvailable()
                if (backendOnline) {
                    repository.syncNotesWithServer(uid)
                } else {
                    Log.w("NotesViewModel", "Backend offline — usando somente dados locais")
                }
            }
        }
    }

    fun insert(note: Note) = viewModelScope.launch {
        userId?.let { uid ->
            val formattedDate = formatDate(LocalDate.now())
            val noteWithFormattedDate = note.copy(date = formattedDate, userId = uid)
            val newId = repository.insert(noteWithFormattedDate)
            Log.d("INSERT", "Nota inserida com ID: $newId")
        }
    }


    // Métodos auxiliares para manejar NoteItem en el ViewModel


    fun insertOrUpdateNote(note: Note) {
        userId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                val isConnected = repository.isBackendAvailable()
                val noteWithUser = note.copy(userId = uid)
                if (isConnected) {
                    if (note.remoteId == null) {
                        repository.insertNoteRemoteAndLocal(noteWithUser)
                    } else if (note.isUpdated) {
                        repository.updateNoteRemoteAndLocal(noteWithUser)
                    } else {
                        Log.d("NotesViewModel", "Nota já sincronizada — nenhuma ação necessária.")
                    }
                } else {
                    val localNote = if (note.remoteId == null) {
                        noteWithUser.copy(isSynced = false)
                    } else {
                        noteWithUser.copy(isUpdated = true, isSynced = false)
                    }
                    repository.insert(localNote)
                }
            }
        }
    }

    fun updateNote(note: Note, context: Context) {
        userId?.let { uid ->
            viewModelScope.launch {
                val noteWithUser = note.copy(userId = uid)
                if (note.remoteId == null) {
                    repository.insert(noteWithUser)
                    Log.e("ViewModel", "Tentou atualizar nota sem remoteId. Inserindo local.")
                } else {
                    repository.updateNoteRemoteAndLocal(noteWithUser)
                }
            }
        }
    }

    fun deleteNote(note: Note, context: Context) {
        userId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                val noteWithUser = note.copy(userId = uid)
                val networkUtils = NetworkUtils()
                if (networkUtils.isConnected(context)) {
                    repository.deleteNoteRemoteAndLocal(noteWithUser)
                } else {
                    val localNote = noteWithUser.copy(isDeleted = true, isSynced = false)
                    repository.updateNoteRemoteAndLocal(localNote)
                }
            }
        }
    }

    fun syncNotesIfNeeded() {
        userId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.syncNotesWithServer(uid)
            }
        }
    }

    fun syncNotesWithServer() {
        userId?.let { uid ->
            viewModelScope.launch(Dispatchers.IO) {
                repository.syncNotesWithServer(uid)
            }
        }
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun getNotesByDate(date: LocalDate): Flow<List<Note>> {
        val formattedDate = formatDate(date)
        return userId?.let { uid ->
            repository.getNotesByDate(formattedDate, uid)
        } ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }
}