package com.borisphen.interviewassistant.data.di

import com.borisphen.interviewassistant.data.InterviewRepositoryImpl
import com.borisphen.interviewassistant.data.NeuralNetworkApi
import com.borisphen.interviewassistant.domain.InterviewRepository
import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DataModule {
    @Binds
    @Singleton
    fun bindRepository(impl: InterviewRepositoryImpl): InterviewRepository

    companion object {
        @Provides
        @Singleton
        fun provideApi(): NeuralNetworkApi = object : NeuralNetworkApi {
            override suspend fun ask(prompt: String): String {
                return "Ответ от модели: $prompt"
            }
        }

        @Provides
        @Singleton
        fun provideRepository(api: NeuralNetworkApi): InterviewRepositoryImpl {
            return InterviewRepositoryImpl(api)
        }

        @Provides
        @Singleton
        fun provideUseCase(repository: InterviewRepository): ProcessInterviewUseCase {
            return ProcessInterviewUseCase(repository)
        }
    }
}