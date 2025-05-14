package com.clouddy.application.data.local.repository

import android.R.attr.id
import android.content.Context
import android.util.Log
import com.clouddy.application.core.utils.NetworkUtils
import com.clouddy.application.data.local.dao.NoteDao
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.data.local.mapper.toNote
import com.clouddy.application.data.network.remote.note.NoteRequestDto
import com.clouddy.application.data.network.remote.note.NotesApiService
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@ViewModelScoped
class NotesRepository @Inject constructor(private val noteDao: NoteDao,
                                          private val api: NotesApiService) {

    val allNotes : Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) = withContext(Dispatchers.IO) {
        noteDao.insertNewNote(note)
    }

    suspend fun update(note: Note) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note)
    }

    suspend fun delete(note: Note) = withContext(Dispatchers.IO) {
        noteDao.delete(note)
    }

    // Métodos auxiliares para manejar NoteItem en el ViewModel
    suspend fun insertNoteRemoteAndLocal(note: Note) = withContext(Dispatchers.IO) {
        val localId = noteDao.insertNewNote(note.copy(isSynced = false))

        try {
            val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), "")
            val response = api.insertNote(dto)

            if (response.isSuccessful) {
                response.body()?.let { savedNote ->
                    val updated = note.copy(
                        id = localId,
                        remoteId = savedNote.id.toString(),
                        isSynced = true
                    )
                    noteDao.updateNote(updated)
                }
            }
        } catch (e: Exception) {
            Log.e("API", "Erro ao conectar com servidor: ${e.message}")
            // Continua salva localmente com isSynced = false
        }
    }


    suspend fun updateNoteRemoteAndLocal(note: Note) = withContext(Dispatchers.IO) {
        if (note.id == null) return@withContext

        try {
            if (!note.remoteId.isNullOrEmpty()) {
                val dto = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId)
                val response = api.updateNote(note.remoteId, dto)

                if (response.isSuccessful) {
                    noteDao.updateNote(note.copy(isSynced = true, isUpdated = false))
                } else {
                    noteDao.updateNote(note.copy(isSynced = false, isUpdated = true))
                }
            } else {
                // Não tem remoteId, mas deve ser salva localmente como atualizada
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
                    noteDao.delete(note) // Deleta do Room definitivamente
                } else {
                    Log.e("API", "Erro ao deletar no servidor: ${response.message()}")
                    val markedNote = note.copy(isDeleted = true, isSynced = false)
                    noteDao.updateNote(markedNote)
                }
            } else {
                // Não tem remoteId → marca como deletada e sincroniza depois
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

    suspend fun syncNotesWithServer() = withContext(Dispatchers.IO) {
        // 1. Sincronizar notas que foram criadas offline (sem remoteId)
        val unsyncedNotes = noteDao.getUnsyncedNotes().filter { !it.isDeleted }

        for (note in unsyncedNotes) {
            try {
                if (note.remoteId.isNullOrEmpty()) {
                    // Criar nova nota no servidor
                    val request = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), "")
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
        val updatedNotes = noteDao.getUpdatedNotes().filter { !it.remoteId.isNullOrEmpty() && !it.isDeleted }

        for (note in updatedNotes) {
            try {
                val request = NoteRequestDto(note.title.orEmpty(), note.note.orEmpty(), note.remoteId!!)
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
        val deletedNotes = noteDao.getDeletedNotes()

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