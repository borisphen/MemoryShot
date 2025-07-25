package com.borisphen.core.domain.note

import com.borisphen.core.domain.note.model.MemoryNote
import kotlinx.coroutines.flow.Flow

interface MemoryNoteRepository {
    suspend fun save(note: MemoryNote)
    fun getAll(): Flow<List<MemoryNote>>
    suspend fun delete(id: Long)
    suspend fun clear()
}