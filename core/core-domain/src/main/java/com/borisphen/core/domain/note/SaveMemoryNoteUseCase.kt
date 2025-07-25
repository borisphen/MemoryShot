package com.borisphen.core.domain.note

import com.borisphen.core.domain.note.model.MemoryNote

class SaveMemoryNoteUseCase(private val repository: MemoryNoteRepository) {
    suspend operator fun invoke(note: MemoryNote) = repository.save(note)
}