package com.borisphen.core.domain.note.model

data class MemoryNote(
    val id: Long = 0,
    val title: String,
    val summary: String,
    val tags: List<String>,
    val originalText: String,
    val createdAt: Long = System.currentTimeMillis()
)