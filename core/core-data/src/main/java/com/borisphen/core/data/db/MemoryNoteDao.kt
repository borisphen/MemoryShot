package com.borisphen.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: MemoryNoteEntity)

    @Query("SELECT * FROM memory_notes ORDER BY createdAt DESC")
    fun getAll(): Flow<List<MemoryNoteEntity>>

    @Query("DELETE FROM memory_notes WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM memory_notes")
    suspend fun clearAll()
}