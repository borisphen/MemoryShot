package com.borisphen.core.data.di

import com.borisphen.core.data.AiRepositoryImpl
import com.borisphen.core.data.network.service.GroqApiService
import com.borisphen.core.domain.ai.AiRepository
import com.borisphen.core.domain.ai.ProcessAiUseCase
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