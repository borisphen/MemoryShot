package com.borisphen.memoryshot.history.presentation.di

import com.borisphen.core.domain.note.DeleteNoteUseCase
import com.borisphen.core.domain.note.GetMemoryNotesUseCase
import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.memoryshot.history.presentation.mapper.MemoryNoteUiMapper
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
internal interface HistoryModule {

    companion object {
        @[Provides Reusable]
        fun provideGetMemoryNotesUseCase(repository: MemoryNoteRepository): GetMemoryNotesUseCase =
            GetMemoryNotesUseCase(repository)

        @[Provides Reusable]
        fun provideDeleteNoteUseCase(repository: MemoryNoteRepository): DeleteNoteUseCase =
            DeleteNoteUseCase(repository)

        @[Provides Reusable]
        fun provideUiMapper(): MemoryNoteUiMapper = MemoryNoteUiMapper()
    }
}