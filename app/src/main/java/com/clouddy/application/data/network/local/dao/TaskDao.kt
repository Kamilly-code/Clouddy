package com.clouddy.application.data.network.local.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.clouddy.application.data.network.local.entity.Task
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNewTask(task: Task): Long

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task_table WHERE isDeleted = 0")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getTaskById(remoteId: String): Task?

    @Query("SELECT * FROM task_table WHERE isSynced = 0 AND isDeleted = 0 AND remoteId IS NULL OR remoteId = ''")
    suspend fun getAllUnsyncedTasks(): List<Task>

    @Query("SELECT * FROM task_table WHERE isDeleted = 1")
    suspend fun getAllDeletedTasks(): List<Task>

    @Query("SELECT * FROM task_table WHERE isUpdated = 1")
    suspend fun getAllUpdatedTasks(): List<Task>

    @Update
    suspend fun updateFull(task: Task)

    @Query("UPDATE task_table SET isCompleted = :completed, isUpdated = 1, isSynced = 0 WHERE id = :taskId")
    suspend fun updateCompletion(taskId: Int, completed: Boolean)

    @Query("SELECT * FROM task_table WHERE date = :selectedDate")
    fun getTasksByDate(selectedDate: String): Flow<List<Task>>


}