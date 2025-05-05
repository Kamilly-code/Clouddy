package com.clouddy.application.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clouddy.application.data.local.dao.NoteDao
import com.clouddy.application.data.local.entity.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDataBase : RoomDatabase() {

    abstract fun getNoteDao() : NoteDao

}