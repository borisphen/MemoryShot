package com.borisphen.memoryshot.history.presentation.model

import androidx.compose.runtime.Stable

@Stable
internal data class MemoryNoteState(
    val id: Long = 0,
    val title: String,
    val summary: String,
    val tags: List<String>,
    val originalText: String,
    val createdAt: Long = System.currentTimeMillis()
)