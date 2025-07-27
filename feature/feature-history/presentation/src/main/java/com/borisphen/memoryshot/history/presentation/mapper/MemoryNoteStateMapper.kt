package com.borisphen.memoryshot.history.presentation.mapper

import com.borisphen.core.data.db.MemoryNoteEntity
import com.borisphen.core.domain.note.model.MemoryNote
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class MemoryNoteStateMapper(private val moshi: Moshi) {
    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(type)

    fun toDomain(entity: MemoryNoteEntity): MemoryNote = with(entity) {
        return MemoryNote(
            id = id,
            title = title,
            summary = summary,
            tags = adapter.fromJson(tags).orEmpty(),
            originalText = originalText,
            createdAt = createdAt
        )
    }

    fun toEntity(note: MemoryNote): MemoryNoteEntity = with(note) {
        return MemoryNoteEntity(
            id = id,
            title = title,
            summary = summary,
            tags = adapter.toJson(tags),
            originalText = originalText,
            createdAt = createdAt
        )
    }
}