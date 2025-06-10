package com.clouddy.application.data.network.local.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.clouddy.application.PreferencesManager
import com.clouddy.application.data.network.local.dao.NoteDao
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.data.network.remote.note.NoteRequestDto
import com.clouddy.application.data.network.remote.note.NotesApiService
import com.clouddy.application.data.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.tasks.await

@ViewModelScoped
class NotesRepository @Inject constructor(private val noteDao: NoteDao,
                                          private val api: NotesApiService,
                                          private val preferencesManager: PreferencesManager,
                                          private val authRepository: AuthRepository) {

     fun getAllNotes(userId: String): Flow<List<Note>> {
        return noteDao.getAllNotes(userId)
    }


    suspend fun insert(note: Note, userId: String) = withContext(Dispatchers.IO) {
        val noteWithUser = note.copy(userId = userId)
        noteDao.insertNewNote(noteWithUser)
    }

    suspend fun update(note: Note) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note)
    }

    suspend fun delete(note: Note) = withContext(Dispatchers.IO) {
        noteDao.delete(note)
    }

    fun getNotesByDate(date: String, userId: String): Flow<List<Note>> =  noteDao.getNotesByDate(date, userId)


    // Métodos auxiliares para manejar NoteItem en el ViewModel
    suspend fun insertNoteRemoteAndLocal(note: Note, userId: String) = withContext(Dispatchers.IO) {
        val token = getAuthToken() ?: return@withContext
        val userId = preferencesManager.getUserId() ?: throw Exception("User not authenticated")
        val localId = noteDao.insertNewNote(note.copy(isSynced = false))

        try {
            val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), "", userId, date = note.date)
            val response = api.insertNote(dto,token)

            if (response.isSuccessful) {
                response.body()?.let { savedNote ->
                    val updated = note.copy(
                        id = localId,
                        remoteId = savedNote.remoteId,
                        userId = userId,
                        isSynced = true
                    )
                    noteDao.updateNote(updated)
                }
            } else {
                Log.e("API", "Erro ao salvar nota no backend: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Erro ao conectar com servidor: ${e.message}")
        }
    }

    private suspend fun getAuthToken(): String? {
        return try {
            val firebaseUser = authRepository.getCurrentUser() ?: return null
            val tokenResult = firebaseUser.getIdToken(false).await()
            "Bearer ${tokenResult.token}"
        } catch (e: Exception) {
            Log.e("NotesRepository", "Failed to get auth token", e)
            null
        }
    }


    suspend fun updateNoteRemoteAndLocal(note: Note, userId: String) = withContext(Dispatchers.IO) {
        val token = getAuthToken() ?: return@withContext
        val userId = preferencesManager.getUserId() ?: return@withContext
        val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId ?: "", userId, date = note.date)
        if (note.id == null) return@withContext

        try {
            if (!note.remoteId.isNullOrEmpty()) {
                val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId!!, userId = note.userId, date = note.date)
                val response = api.updateNote(note.remoteId, dto,token)

                if (response.isSuccessful) {
                    noteDao.updateNote(note.copy(isSynced = true, isUpdated = false))
                } else {
                    noteDao.updateNote(note.copy(isSynced = false, isUpdated = true))
                }
            } else {
                noteDao.updateNote(note.copy(isSynced = false, isUpdated = true))
            }
        } catch (e: Exception) {
            Log.e("API", "Erro ao atualizar: ${e.message}")
            noteDao.updateNote(note.copy(isSynced = false, isUpdated = true))
        }
    }


    suspend fun deleteNoteRemoteAndLocal(note: Note, userId: String) = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext
            if (note.remoteId != null && note.remoteId.isNotEmpty()) {
                val response = api.deleteNote(note.remoteId, token)
                if (response.isSuccessful) {
                    noteDao.delete(note)
                } else {
                    Log.e("API", "Erro ao deletar no servidor: ${response.message()}")
                    noteDao.delete(note)
                }
            } else {
                noteDao.delete(note)
            }
        } catch (e: Exception) {
            Log.e("API", "Erro de conexão: ${e.message}")
            noteDao.delete(note)
        }
    }



    suspend fun isBackendAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("http://10.0.2.2:4000/ping")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 1000
            connection.readTimeout = 1000
            connection.requestMethod = "GET"
            connection.connect()
            connection.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }

    suspend fun syncNotesWithServer(userId: String) = withContext(Dispatchers.IO) {
        // 1. Sincronizar notas que foram criadas offline (sem remoteId)
        val unsyncedNotes = noteDao.getUnsyncedNotes(userId).filter { !it.isDeleted && it.userId == userId }

        for (note in unsyncedNotes) {
            try {
                if (note.remoteId.isNullOrEmpty()) {
                    // Criar nova nota no servidor
                    val request = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), "", userId, date = note.date)
                    val response = api.insertNote(request, getAuthToken() ?: "")

                    if (response.isSuccessful) {
                        response.body()?.let { serverNote ->
                            val syncedNote = note.copy(
                                remoteId = serverNote.id.toString(),
                                isSynced = true,
                                isUpdated = false
                            )
                            noteDao.updateNote(syncedNote)
                        }
                    } else {
                        Log.e("Sync", "Erro ao criar nota: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao criar nota (id=${note.id}): ${e.message}")
            }
        }

        // 2. Sincronizar notas editadas offline
        val updatedNotes = noteDao.getUpdatedNotes(userId).filter { !it.remoteId.isNullOrEmpty() && !it.isDeleted }

        for (note in updatedNotes) {
            try {
                val request = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId!!, userId, date = note.date)
                val response = api.updateNote(note.remoteId!!, request, getAuthToken() ?: "")

                if (response.isSuccessful) {
                    val syncedNote = note.copy(
                        isSynced = true,
                        isUpdated = false
                    )
                    noteDao.updateNote(syncedNote)
                } else {
                    Log.e("Sync", "Erro ao atualizar nota remota: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao atualizar nota (id=${note.id}): ${e.message}")
            }
        }

        // 3. Sincronizar notas deletadas offline
        val deletedNotes = noteDao.getDeletedNotes(userId)

        for (note in deletedNotes) {
            try {
                if (!note.remoteId.isNullOrEmpty()) {
                    val response = api.deleteNote(note.remoteId!!, getAuthToken() ?: "")
                    if (response.isSuccessful) {
                        noteDao.delete(note)
                    } else {
                        Log.e("Sync", "Erro ao deletar no servidor: ${response.message()}")
                    }
                } else {
                    // Nota nunca foi sincronizada: deletar direto
                    noteDao.delete(note)
                }
            } catch (e: Exception) {
                Log.e("Sync", "Falha ao deletar nota (id=${note.id}): ${e.message}")
            }
        }
    }
}