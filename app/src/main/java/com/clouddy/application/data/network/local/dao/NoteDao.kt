package com.clouddy.application.data.network.local.dao


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

    @Query("SELECT * FROM notes_table WHERE isDeleted = 0 AND userId = :userId")
    fun getAllNotes(userId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE remoteId = :remoteId AND userId = :userId LIMIT 1")
    suspend fun getNoteById(remoteId: String, userId: String): Note?

    @Query("SELECT * FROM notes_table WHERE isSynced = 0 AND isDeleted = 0 AND userId = :userId")
    suspend fun getUnsyncedNotes(userId: String): List<Note>


    @Query("SELECT * FROM notes_table WHERE remoteId = :remoteId AND userId = :userId LIMIT 1")
    suspend fun getNoteByRemoteId(remoteId: String, userId: String): Note?

    @Query("SELECT * FROM notes_table WHERE isDeleted = 1 AND userId = :userId")
    suspend fun getDeletedNotes(userId: String): List<Note>

    @Query("SELECT * FROM notes_table WHERE isUpdated = 1 AND userId = :userId")
    suspend fun getUpdatedNotes(userId: String): List<Note>

    @Query("SELECT * FROM notes_table WHERE date = :date AND userId = :userId")
    fun getNotesByDate(date: String, userId: String): Flow<List<Note>>

    @Query("DELETE FROM notes_table WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Long, userId: String)

    @Query("DELETE FROM notes_table WHERE remoteId = :remoteId AND userId = :userId")
    suspend fun deleteByRemoteId(remoteId: String, userId: String)

    @Query("DELETE FROM notes_table WHERE userId = :userId")
    suspend fun deleteAllNotesForUser(userId: String)

}