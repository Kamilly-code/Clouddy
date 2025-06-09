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

    @Query("SELECT * FROM task_table WHERE isDeleted = 0 AND userId = :userId")
    fun getAllTasks(userId: String): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE remoteId = :remoteId AND userId = :userId LIMIT 1")
    suspend fun getTaskById(remoteId: String, userId: String): Task?

    @Query("SELECT * FROM task_table WHERE isSynced = 0 AND isDeleted = 0 AND userId = :userId AND (remoteId IS NULL OR remoteId = '')")
    suspend fun getAllUnsyncedTasks(userId: String): List<Task>


    @Query("SELECT * FROM task_table WHERE isDeleted = 1 AND userId = :userId")
    suspend fun getAllDeletedTasks(userId: String): List<Task>

    @Query("SELECT * FROM task_table WHERE isUpdated = 1 AND userId = :userId")
    suspend fun getAllUpdatedTasks(userId: String): List<Task>

    @Update
    suspend fun updateFull(task: Task)

    @Query("UPDATE task_table SET isCompleted = :completed, isUpdated = 1, isSynced = 0 WHERE id = :taskId AND userId = :userId")
    suspend fun updateCompletion(taskId: Int, completed: Boolean, userId: String)

    @Query("SELECT * FROM task_table WHERE date = :selectedDate AND userId = :userId")
    fun getTasksByDate(selectedDate: String, userId: String): Flow<List<Task>>
}