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
    suspend fun insert(task: Task): Long

    @Delete
    suspend fun delete(task: Task)

    @Query("UPDATE task_table SET isCompleted = :completed WHERE id = :taskId")
    suspend fun update(taskId: Int, completed: Boolean)

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE date = :selectedDate")
    fun getTasksByDate(selectedDate: String): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE isSynced = 0")
    suspend fun getAllUnsyncedTasks(): List<Task>

    @Query("SELECT * FROM task_table WHERE isUpdated = 1")
    suspend fun getAllUpdatedTasks(): List<Task>

    @Query("SELECT * FROM task_table WHERE isDeleted = 1")
    suspend fun getAllDeletedTasks(): List<Task>

    @Update
    suspend fun updateFull(task: Task)


}