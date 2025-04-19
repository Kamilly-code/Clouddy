package com.clouddy.application.di

import android.content.Context
import androidx.room.Room
import com.clouddy.application.core.utils.DATABASE_TASK
import com.clouddy.application.data.local.dao.TaskDao
import com.clouddy.application.data.local.db.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DbModuleTask {

    @Provides
    @Singleton
    fun provideDatabaseTask(@ApplicationContext context : Context): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            DATABASE_TASK
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDaoTask(database: TaskDatabase) : TaskDao = database.taskDao()

}