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
import java.util.UUID
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
                syncNotesWithServer()
            }
        }
    }


    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
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

    fun insert(note: Note)= viewModelScope.launch {
        val userId = currentUserId.value ?: return@launch
        val formattedDate = formatDate(LocalDate.now())

        val noteWithUser = note.copy(
            date = formattedDate,
            userId = userId,
            isSynced = false,
            remoteId = note.remoteId ?: UUID.randomUUID().toString()
        )

        try {
            val isOnline = repository.isBackendAvailable()

            if (isOnline) {
                Log.d("INSERT", "Online: tentando inserir local e remoto")
                repository.insertNoteRemoteAndLocal(noteWithUser, userId)
            } else {
                Log.d("INSERT", "Offline: salvando localmente")
                repository.insert(noteWithUser, userId)
                repository.syncNotesWithServer(userId)
            }

            // ✅ Força atualização da lista de notas após insert
            loadNotes(userId)

        } catch (e: Exception) {
            Log.e("NotesViewModel", "Erro ao inserir nota", e)
        }
    }

    fun insertOrUpdateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = currentUserId.value ?: throw IllegalStateException("User not authenticated")
                val noteWithUser = note.copy(userId = userId)
                val noteWithRemoteId = if (noteWithUser.remoteId.isNullOrEmpty()) {
                    noteWithUser.copy(remoteId = UUID.randomUUID().toString())
                } else {
                    noteWithUser
                }

                val isConnected = repository.isBackendAvailable()
                if (isConnected) {
                    repository.insertNoteRemoteAndLocal(noteWithRemoteId, userId)
                } else {
                    repository.insert(noteWithRemoteId.copy(isSynced = false), userId)
                }

                // ✅ Atualiza a lista após inserir ou editar
                viewModelScope.launch {
                    loadNotes(userId)
                }

            } catch (e: Exception) {
                Log.e("NotesViewModel", "Erro ao inserir/atualizar nota", e)
            }
        }
    }
fun updateNote(note: Note, context: Context) {
    currentUserId.value?.let { userId ->
        viewModelScope.launch {
            try {
                val noteWithUser = note.copy(userId = userId)
                if (note.remoteId == null) {
                    repository.insert(noteWithUser, userId)
                    Log.e("ViewModel", "Tentou atualizar nota sem remoteId. Inserindo local.")
                } else {
                    repository.updateNoteRemoteAndLocal(noteWithUser, userId)
                    loadNotes(userId)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Erro ao atualizar nota", e)
            }
        }
    }
}

    fun deleteNote(note: Note, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserId.value?.let { userId ->
                val noteWithUser = note.copy(userId = userId)
                val networkUtils = NetworkUtils()

                repository.delete(noteWithUser)

                if (networkUtils.isConnected(context) && !note.remoteId.isNullOrEmpty()) {
                    try {
                        repository.deleteNoteRemoteAndLocal(noteWithUser, userId)
                    } catch (e: Exception) {
                        Log.e("NotesViewModel", "Failed to delete note remotely", e)
                    }
                }

                // ✅ Atualiza a lista após deletar
                loadNotes(userId)
            }
        }
    }


    fun syncNotesIfNeeded() {
        currentUserId.value?.let { userId ->
            viewModelScope.launch {
                repository.syncNotesWithServer(userId)
                loadNotes(userId)
            }
        }
    }

    fun syncNotesWithServer() {
        currentUserId.value?.let { userId ->
            viewModelScope.launch {
                repository.syncNotesWithServer(userId)
                loadNotes(userId)
            }
        }
    }


    fun getNotesByDate(date: LocalDate): Flow<List<Note>> {
        val formattedDate = formatDate(date)
        return currentUserId.value?.let { userId ->
            repository.getNotesByDate(formattedDate, userId)
        } ?: flowOf(emptyList())
    }
}