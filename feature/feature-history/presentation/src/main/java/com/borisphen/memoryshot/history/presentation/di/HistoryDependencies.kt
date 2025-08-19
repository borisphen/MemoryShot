package com.borisphen.memoryshot.history.presentation.di

import com.borisphen.core.domain.note.MemoryNoteRepository

interface HistoryDependencies {
    val memoryNoteRepository: MemoryNoteRepository
}