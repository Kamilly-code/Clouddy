package com.clouddy.application.data.local.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clouddy.application.data.local.entity.Task
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("UPDATE task_table SET isCompleted = :completed WHERE id = :taskId")
    suspend fun update(taskId: Int, completed: Boolean)

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): Flow<List<Task>>

}