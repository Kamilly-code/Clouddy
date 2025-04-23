package com.clouddy.application.data.local.repository

import androidx.lifecycle.LiveData
import com.clouddy.application.data.local.dao.NoteDao
import com.clouddy.application.data.local.entity.Note
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject
import jakarta.inject.Singleton

@ViewModelScoped
class NotesRepository @Inject constructor(private val noteDao: NoteDao) {
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

}