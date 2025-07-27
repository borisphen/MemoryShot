package com.borisphen.core.domain.note

class DeleteNoteUseCase(private val repository: MemoryNoteRepository) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}