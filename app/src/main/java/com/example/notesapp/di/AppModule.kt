package com.example.notesapp.di

import com.example.notesapp.util.AppLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppLogger(): AppLogger {
        return AppLogger("NotesAppDebugTag")
    }
}