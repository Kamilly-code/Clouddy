package com.clouddy.application.di

import android.content.Context
import androidx.room.Room
import com.clouddy.application.core.utils.DATABASE_NOTE
import com.clouddy.application.data.local.dao.NoteDao
import com.clouddy.application.data.local.db.NoteDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DbModuleNote {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : NoteDataBase {
        return Room.databaseBuilder(
            context,
            NoteDataBase::class.java,
            DATABASE_NOTE
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideNoteDao(database: NoteDataBase) : NoteDao {
        return database.getNoteDao()
    }
}