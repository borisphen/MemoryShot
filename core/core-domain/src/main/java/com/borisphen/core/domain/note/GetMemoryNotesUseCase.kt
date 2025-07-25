package com.borisphen.core.domain.note

import com.borisphen.core.domain.note.model.MemoryNote
import kotlinx.coroutines.flow.Flow

class GetMemoryNotesUseCase(private val repository: MemoryNoteRepository) {
    operator fun invoke(): Flow<List<MemoryNote>> = repository.getAll()
}