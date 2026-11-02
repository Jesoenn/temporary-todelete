package com.example.pdf_reader.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DocumentEntity)

    @Query("SELECT * FROM documents ORDER BY lastOpenedEpochMs DESC")
    fun observeRecent(): Flow<List<DocumentEntity>>
}
