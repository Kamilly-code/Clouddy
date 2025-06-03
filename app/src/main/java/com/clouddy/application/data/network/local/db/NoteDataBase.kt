package com.clouddy.application.data.network.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.clouddy.application.data.network.local.dao.NoteDao
import com.clouddy.application.data.network.local.entity.Note

@Database(entities = [Note::class], version = 11, exportSchema = true)
abstract class NoteDataBase : RoomDatabase() {

    abstract fun getNoteDao() : NoteDao

}