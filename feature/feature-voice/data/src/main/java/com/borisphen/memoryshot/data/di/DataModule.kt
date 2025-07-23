package com.borisphen.memoryshot.data.di

import com.borisphen.memoryshot.data.AiRepositoryImpl
import com.borisphen.memoryshot.data.service.GroqApiService
import com.borisphen.memoryshot.domain.AiRepository
import com.borisphen.memoryshot.domain.ProcessAiUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DataModule {
    @Binds
    @Singleton
    fun bindRepository(impl: AiRepositoryImpl): AiRepository

    companion object {

        @Provides
        @Singleton
        fun provideRepository(service: GroqApiService): AiRepositoryImpl {
            return AiRepositoryImpl(service)
        }

        @Provides
        @Singleton
        fun provideUseCase(repository: AiRepository): ProcessAiUseCase {
            return ProcessAiUseCase(repository)
        }
    }
}