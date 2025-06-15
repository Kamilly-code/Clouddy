package com.clouddy.application.data.network.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.clouddy.application.data.network.local.entity.Pomodoro
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPomodoro(pomodoro: Pomodoro) : Long

    @Update
    suspend fun updatePomodoro(pomodoro: Pomodoro)

    @Query("SELECT * FROM pomodoro_table WHERE userId = :userId AND id = 1 LIMIT 1")
     fun getPomodoroSettings(userId: String): Flow<Pomodoro?>

    @Query("DELETE FROM pomodoro_table WHERE userId = :userId")
    suspend fun deleteAll(userId: String)

    @Query("SELECT * FROM pomodoro_table WHERE userId = :userId AND id = 1")
    fun getTotalFocusTime(userId: String): Flow<Pomodoro?>

    @Query("UPDATE pomodoro_table SET totalMinutes = totalMinutes + :minutes WHERE userId = :userId AND id = 1")
    suspend fun addFocusMinutes(minutes: Int, userId: String)

    @Query("SELECT * FROM pomodoro_table WHERE userId = :userId")
    fun getAllPomodoro(userId: String): Flow<List<Pomodoro>>

    @Query("SELECT * FROM pomodoro_table WHERE remoteId = :remoteId AND userId = :userId LIMIT 1")
    suspend fun getPomodoroByRemoteId(remoteId: String, userId: String): Pomodoro?

    @Update
    suspend fun updatePomodoroByRemoteId(pomodoro: Pomodoro)


}