package com.borisphen.memoryshot.history.presentation.di

import com.borisphen.core.domain.note.DeleteNoteUseCase
import com.borisphen.core.domain.note.GetMemoryNotesUseCase
import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.core.domain.note.SaveMemoryNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
interface HistoryModule {

    companion object {
        @[Provides Reusable]
        fun provideGetMemoryNotesUseCase(repository: MemoryNoteRepository): GetMemoryNotesUseCase =
            GetMemoryNotesUseCase(repository)

        @[Provides Reusable]
        fun provideDeleteNoteUseCase(repository: MemoryNoteRepository): DeleteNoteUseCase =
            DeleteNoteUseCase(repository)

        @[Provides Reusable]
        fun provideSaveMemoryNoteUseCase(repository: MemoryNoteRepository): SaveMemoryNoteUseCase =
            SaveMemoryNoteUseCase(repository)
    }
}