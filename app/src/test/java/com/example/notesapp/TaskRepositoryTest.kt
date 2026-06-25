package com.example.notesapp

import com.example.notesapp.data.TaskDao
import com.example.notesapp.model.Task
import com.example.notesapp.repository.TaskRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify

class TaskRepositoryTest {

    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepository

    @Before
    fun setUp() {

        taskDao = Mockito.mock(TaskDao::class.java)
        repository = TaskRepository(taskDao)
    }

    @Test
    fun insertTask_shouldCallDaoInsert() = runTest {

        val dummyTask = Task(id = "1", title = "Zadaća 9", body = "Napisati testove")

        repository.insertTask(dummyTask)

        verify(taskDao).insertTasks(listOf(dummyTask))
    }
}