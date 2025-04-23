package com.clouddy.application.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clouddy.application.core.utils.DATABASE_NOTE
import com.clouddy.application.data.local.dao.NoteDao
import com.clouddy.application.data.local.entity.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDataBase : RoomDatabase() {

    abstract fun getNoteDao() : NoteDao

}