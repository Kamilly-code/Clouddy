package com.clouddy.application.di

import android.content.Context
import androidx.room.Room
import com.clouddy.application.core.utils.DATABASE_NOTE
import com.clouddy.application.core.utils.DATABASE_POMODORO
import com.clouddy.application.data.local.dao.NoteDao
import com.clouddy.application.data.local.dao.PomodoroDao
import com.clouddy.application.data.local.db.NoteDataBase
import com.clouddy.application.data.local.db.PomodoroDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModulePomodoro {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : PomodoroDatabase {
        return Room.databaseBuilder(
            context,
            PomodoroDatabase::class.java,
            DATABASE_POMODORO
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun providePomodoroDao(database: PomodoroDatabase) : PomodoroDao {
        return database.pomodoroDao()
    }
}