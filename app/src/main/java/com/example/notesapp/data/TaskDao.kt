package com.example.notesapp.data

import androidx.room.*
import com.example.notesapp.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks_table")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Query("DELETE FROM tasks_table")
    suspend fun deleteAllTasks()
}