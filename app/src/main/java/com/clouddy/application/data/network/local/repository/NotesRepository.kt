package com.clouddy.application.data.network.local.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.clouddy.application.PreferencesManager
import com.clouddy.application.data.network.local.dao.NoteDao
import com.clouddy.application.data.network.local.entity.Note
import com.clouddy.application.data.network.remote.note.NoteRequestDto
import com.clouddy.application.data.network.remote.note.NotesApiService
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.flow.flowOf

@ViewModelScoped
class NotesRepository @Inject constructor(private val noteDao: NoteDao,
                                          private val api: NotesApiService,
                                          private val preferencesManager: PreferencesManager) {

    fun getAllNotes(): Flow<List<Note>> {
        val userId = preferencesManager.getUserId() ?: return flowOf(emptyList<Note>())
        return noteDao.getAllNotes(userId)
    }

    suspend fun insert(note: Note) = withContext(Dispatchers.IO) {
        noteDao.insertNewNote(note)
    }

    suspend fun update(note: Note) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note)
    }

    suspend fun delete(note: Note) = withContext(Dispatchers.IO) {
        noteDao.delete(note)
    }

    fun getNotesByDate(date: String, userId: String): Flow<List<Note>> =  noteDao.getNotesByDate(date, userId)


    // Métodos auxiliares para manejar NoteItem en el ViewModel
    suspend fun insertNoteRemoteAndLocal(note: Note) = withContext(Dispatchers.IO) {
        val userId = preferencesManager.getUserId() ?: throw Exception("User not authenticated")
        val localId = noteDao.insertNewNote(note.copy(isSynced = false))

        try {
            val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), "", userId)
            val response = api.insertNote(dto)

            if (response.isSuccessful) {
                response.body()?.let { savedNote ->
                    val updated = note.copy(
                        id = localId,
                        remoteId = savedNote.id.toString(),
                        userId = userId,
                        isSynced = true
                    )
                    noteDao.updateNote(updated)
                }
            }
        } catch (e: Exception) {
            Log.e("API", "Erro ao conectar com servidor: ${e.message}")
        }
    }


    suspend fun updateNoteRemoteAndLocal(note: Note) = withContext(Dispatchers.IO) {
        val userId = preferencesManager.getUserId() ?: return@withContext
        val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId ?: "", userId)
        if (note.id == null) return@withContext

        try {
            if (!note.remoteId.isNullOrEmpty()) {
                val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId!!, userId = note.userId)
                val response = api.updateNote(note.remoteId, dto)

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


    suspend fun deleteNoteRemoteAndLocal(note: Note) = withContext(Dispatchers.IO) {
        try {
            if (note.remoteId != null && note.remoteId.isNotEmpty()) {
                val response = api.deleteNote(note.remoteId)
                if (response.isSuccessful) {
                    noteDao.delete(note)
                } else {
                    Log.e("API", "Erro ao deletar no servidor: ${response.message()}")
                    val markedNote = note.copy(isDeleted = true, isSynced = false)
                    noteDao.updateNote(markedNote)
                }
            } else {
                val markedNote = note.copy(isDeleted = true, isSynced = false)
                noteDao.updateNote(markedNote)
            }
        } catch (e: Exception) {
            Log.e("API", "Erro de conexão: ${e.message}")
            val markedNote = note.copy(isDeleted = true, isSynced = false)
            noteDao.updateNote(markedNote)
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
        val unsyncedNotes = noteDao.getUnsyncedNotes(userId).filter { !it.isDeleted }

        for (note in unsyncedNotes) {
            try {
                if (note.remoteId.isNullOrEmpty()) {
                    // Criar nova nota no servidor
                    val request = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), "", userId)
                    val response = api.insertNote(request)

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
                val request = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId!!, userId)
                val response = api.updateNote(note.remoteId!!, request)

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
                    val response = api.deleteNote(note.remoteId!!)
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