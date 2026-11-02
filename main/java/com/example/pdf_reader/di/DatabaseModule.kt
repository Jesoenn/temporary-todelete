package com.example.pdf_reader.di

import android.content.Context
import androidx.room.Room
import com.example.pdf_reader.data.local.db.AppDatabase
import com.example.pdf_reader.data.local.db.DocumentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pdf_reader.db",
        ).build()
    }

    @Provides
    fun provideDocumentDao(database: AppDatabase): DocumentDao = database.documentDao()
}
