package com.clouddy.application.data.network.local.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.clouddy.application.data.network.local.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewNote(note: Note): Long

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun updateNote(note: Note)


    @Query("SELECT * FROM notes_table WHERE isDeleted = 0")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getNoteById(remoteId: String): Note?

    @Query("SELECT * FROM notes_table WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedNotes(): List<Note>


    @Query("SELECT * FROM notes_table WHERE isDeleted = 1")
    suspend fun getDeletedNotes(): List<Note>

    @Query("SELECT * FROM notes_table WHERE isUpdated = 1 ")
    suspend fun getUpdatedNotes(): List<Note>

    @Query("SELECT * FROM notes_table WHERE date = :date")
    fun getNotesByDate(date: String): Flow<List<Note>>


}