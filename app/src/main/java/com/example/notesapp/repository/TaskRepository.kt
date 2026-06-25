package com.example.notesapp.repository

import com.example.notesapp.data.TaskDao
import com.example.notesapp.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasksFlow() = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTasks(listOf(task))
    }
}