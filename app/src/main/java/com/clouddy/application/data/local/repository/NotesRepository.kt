package com.clouddy.application.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.clouddy.application.data.local.dao.NoteDao
import com.clouddy.application.data.local.entity.Note
import com.clouddy.application.data.local.mapper.toNote
import com.clouddy.application.data.local.mapper.toNoteRequestDto
import com.clouddy.application.data.network.remote.note.NoteRequestDto
import com.clouddy.application.data.network.remote.note.NotesApiService
import com.clouddy.application.domain.model.NoteItem
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject

@ViewModelScoped
class NotesRepository @Inject constructor(private val noteDao: NoteDao,
                                          private val api: NotesApiService) {
    val allNotes : LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note){
        noteDao.insert(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note.id, note.title, note.note)
    }

    // Métodos auxiliares para manejar NoteItem en el ViewModel
    suspend fun insertNoteRemoteAndLocal(noteRequestDto: NoteRequestDto) {
        try {
            val response = api.insertNote(noteRequestDto)
            if (response.isSuccessful) {
                val savedNote = response.body()
                savedNote?.let {
                    val noteEntity = it.toNote()
                    noteDao.insert(noteEntity)
                }
            } else {
                Log.e("API", "Error al insertar nota: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("API", "Error al conectar al servidor: ${e.message}")
        }
    }

    suspend fun updateNoteRemoteAndLocal(noteItem: NoteItem) : Boolean {
        val id = noteItem.id ?: return false
        return try {
            val noteRequestDto = noteItem.toNoteRequestDto()
            val response = api.updateNote(id, noteRequestDto)
            if (response.isSuccessful && response.body() != null) {
                val updatedNote = response.body()!!
                noteDao.insert(updatedNote.toNote())
                true
            } else {
                Log.e("API", "Erro ao atualizar: ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("API", "Erro de conexão: ${e.message}")
            false
        }
    }

    suspend fun deleteNoteRemoteAndLocal(noteItem: NoteItem) : Boolean {
        val id = noteItem.id ?: return false
        return try {
            val response = api.deleteNote(id)
            if (response.isSuccessful) {
                noteDao.delete(noteItem.toNote())
                true
            } else {
                Log.e("API", "Erro ao deletar: ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("API", "Erro de conexão: ${e.message}")
            false
        }
    }
}