package com.example.notesapp.api

import com.example.notesapp.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TaskApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("tasks/all")
    suspend fun getAllTasks(@Header("Authorization") token: String): TasksResponse // Vraća omot s tasks listom!

    @POST("tasks/create")
    suspend fun createTask(@Header("Authorization") token: String, @Body request: TaskRequest): Task

    @GET("tasks/{id}")
    suspend fun getTaskById(@Header("Authorization") token: String, @Path("id") id: String): Task

    @PUT("tasks/{id}")
    suspend fun updateTask(@Header("Authorization") token: String, @Path("id") id: String, @Body request: TaskRequest): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Header("Authorization") token: String, @Path("id") id: String)

    companion object {
        private const val BASE_URL = "https://ada-taskie-backend.osc-fr1.scalingo.io/"

        fun create(): TaskApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TaskApiService::class.java)
        }
    }
}