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
import com.clouddy.application.data.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.text.format
import kotlin.text.insert

@HiltViewModel
class NotesViewModel @Inject constructor(private val repository: NotesRepository,
                                         private val preferencesManager: PreferencesManager,
                                         private val authRepository: AuthRepository) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(preferencesManager.getUserId() != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteItem>>(emptyList())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()


    init {
        viewModelScope.launch {
            val userId = getUserId() ?: run {
                Log.e("NotesViewModel", "UserId não encontrado")
                _isAuthenticated.value = false
                return@launch
            }

            _currentUserId.value = userId
            _isAuthenticated.value = true

            loadNotes(userId)
            if (repository.isBackendAvailable()) {
                syncNotesWithServer(userId)
            }
        }
    }


        private fun getUserId(): String? {
        val userId = preferencesManager.getUserId()
        if (userId.isNullOrEmpty()) {
            Log.e("NotesViewModel", "UserID vazio. Tentando recuperar do Firebase...")
            val firebaseUser = authRepository.getCurrentUser()
            firebaseUser?.uid?.let { uid ->
                preferencesManager.saveUserId(uid)
                return uid
            }
            return null
        }
        return userId
    }

    internal fun loadNotes(userId: String) {
        viewModelScope.launch {
            try {
                repository.getAllNotes(userId)
                    .map { notes -> notes.map { it.toNoteItem() } }
                    .collect { notesList ->
                        _notes.value = notesList
                    }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Error loading notes", e)
            }
        }
    }



    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    fun insert(note: Note) = viewModelScope.launch {
        currentUserId.value?.let { userId ->
            val formattedDate = formatDate(LocalDate.now())
            val noteWithUser = note.copy(
                date = formattedDate,
                userId = userId,
                isSynced = false
            )
            val newId = repository.insert(noteWithUser, userId)
            Log.d("INSERT", "Nota inserida com ID: $newId")
        } ?: run {
            Log.e("INSERT", "Tentativa de inserir nota sem usuário autenticado")
        }
    }

    fun insertOrUpdateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserId.value?.let { userId ->
                val noteWithUser = note.copy(userId = userId)
                val isConnected = repository.isBackendAvailable()

                if (isConnected) {
                    if (note.remoteId == null) {
                        repository.insertNoteRemoteAndLocal(noteWithUser, userId)
                    } else if (note.isUpdated) {
                        repository.updateNoteRemoteAndLocal(noteWithUser, userId)
                    } else {
                        Log.d("NotesViewModel", "Nota já sincronizada")
                    }
                } else {
                    val localNote = if (note.remoteId == null) {
                        noteWithUser.copy(isSynced = false)
                    } else {
                        noteWithUser.copy(isUpdated = true, isSynced = false)
                    }
                    repository.insert(localNote, userId)
                }

                // FORÇAR atualização da lista após inserção/atualização
                repository.getAllNotes(userId).collect { notes ->
                    _notes.value = notes.map { it.toNoteItem() }
                }
            }
        }
    }


fun updateNote(note: Note, context: Context) {
    viewModelScope.launch {
        currentUserId.value?.let { userId ->
        val noteWithUser = note.copy(userId =  userId)
        if (note.remoteId == null) {
            repository.insert(noteWithUser, userId)
            Log.e("ViewModel", "Tentou atualizar nota sem remoteId. Inserindo local.")
        } else {
            repository.updateNoteRemoteAndLocal(noteWithUser, userId)
        }
    }
}
}

    fun deleteNote(note: Note, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserId.value?.let { userId ->
                val noteWithUser = note.copy(userId = userId)
                val networkUtils = NetworkUtils()

                // Sempre deleta localmente primeiro
                repository.delete(noteWithUser)

                // Se tiver conexão e remoteId, tenta deletar no servidor
                if (networkUtils.isConnected(context) && !note.remoteId.isNullOrEmpty()) {
                    try {
                        repository.deleteNoteRemoteAndLocal(noteWithUser, userId)
                    } catch (e: Exception) {
                        Log.e("NotesViewModel", "Failed to delete note remotely", e)
                    }
                }
            }
        }
    }

    fun syncNotesIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserId.value?.let { userId ->
                repository.syncNotesWithServer(userId)
            }
        }
    }

    fun syncNotesWithServer(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserId.value?.let { userId ->
                repository.syncNotesWithServer(userId)
            }
        }

        fun formatDate(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return date.format(formatter)
        }

        fun getNotesByDate(date: LocalDate): Flow<List<Note>> {
            val formattedDate = formatDate(date)
            return currentUserId.value?.let { userId ->
                repository.getNotesByDate(formattedDate, userId)
            } ?: flowOf(emptyList())
        }
    }
}