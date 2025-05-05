package com.clouddy.application.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clouddy.application.data.local.entity.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)


    @Query("SELECT * FROM notes_table ORDER BY id ASC")
    fun getAllNotes() : LiveData<List<Note>>


    @Query("UPDATE notes_table SET title = :title , note = :note WHERE id = :id")
    suspend fun update(id: Long?, title : String?, note : String?)
}