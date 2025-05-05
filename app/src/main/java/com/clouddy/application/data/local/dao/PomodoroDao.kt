package com.clouddy.application.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.clouddy.application.data.local.entity.Pomodoro
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPomodoro(pomodoro: Pomodoro)

    @Update
    suspend fun updatePomodoro(pomodoro: Pomodoro)

    @Query("SELECT * FROM pomodoro_table LIMIT 1")
     fun getPomodoroSettings(): Flow<Pomodoro?>

    @Query("DELETE FROM pomodoro_table")
    suspend fun deleteAll()


}