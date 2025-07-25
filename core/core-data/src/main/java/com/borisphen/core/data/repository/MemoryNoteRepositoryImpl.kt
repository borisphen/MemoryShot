package com.borisphen.core.data.repository

import com.borisphen.core.data.db.MemoryNoteDao
import com.borisphen.core.data.mapper.MemoryNoteMapper
import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.core.domain.note.model.MemoryNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MemoryNoteRepositoryImpl(
    private val dao: MemoryNoteDao,
    private val mapper: MemoryNoteMapper
) : MemoryNoteRepository {
    override suspend fun save(note: MemoryNote) {
        dao.insert(mapper.toEntity(note))
    }

    override fun getAll(): Flow<List<MemoryNote>> {
        return dao.getAll().map { list -> list.map { mapper.toDomain(it) } }
    }

    override suspend fun delete(id: Long) {
        dao.delete(id)
    }

    override suspend fun clear() {
        dao.clearAll()
    }
}