package com.borisphen.memoryshot.history.presentation.di

import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.core.domain.service.ServiceController
import com.borisphen.core.domain.speech.RecognizerEngine

interface HistoryDependencies {
    val memoryNoteRepository: MemoryNoteRepository
}