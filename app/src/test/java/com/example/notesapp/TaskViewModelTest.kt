package com.example.notesapp

import android.app.Application
import com.example.notesapp.util.AppLogger
import com.example.notesapp.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private lateinit var application: Application
    private lateinit var logger: AppLogger
    private lateinit var viewModel: TaskViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {

        Dispatchers.setMain(testDispatcher)

        application = mock(Application::class.java)

        org.mockito.kotlin.whenever(application.applicationContext).thenReturn(application)

        val tempDir = java.io.File(System.getProperty("java.io.tmpdir"))
        org.mockito.kotlin.whenever(application.filesDir).thenReturn(tempDir)

        logger = mock(AppLogger::class.java)

        viewModel = TaskViewModel(application, logger)
    }

    @After
    fun tearDown() {

        Dispatchers.resetMain()
    }

    @Test
    fun viewModel_initialization_shouldLogMessage() {

        verify(logger).logI("TaskViewModel inicijaliziran pomoću Hilt DI-ja.")
    }
}