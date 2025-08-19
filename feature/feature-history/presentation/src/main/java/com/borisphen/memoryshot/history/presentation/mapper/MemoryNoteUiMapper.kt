package com.borisphen.memoryshot.history.presentation.mapper

import com.borisphen.core.domain.note.model.MemoryNote
import com.borisphen.memoryshot.history.presentation.model.MemoryNoteState

internal class MemoryNoteUiMapper {

    fun toMemoryNoteState(note: MemoryNote): MemoryNoteState = with(note) {
        return MemoryNoteState(
            id = id,
            title = title,
            summary = summary,
            tags = tags,
            originalText = originalText,
            createdAt = createdAt
        )
    }

    fun toMemoryNoteEntity(noteState: MemoryNoteState): MemoryNote = with(noteState) {
        return MemoryNote(
            id = id,
            title = title,
            summary = summary,
            tags = tags,
            originalText = originalText,
            createdAt = createdAt
        )
    }
}