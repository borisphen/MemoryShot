package com.borisphen.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_notes")
data class MemoryNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val summary: String,
    val tags: String, // JSON array
    val originalText: String,
    val createdAt: Long,
    val ocrText: String? = null,
    val screenshotPath: String? = null
)
