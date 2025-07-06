package com.borisphen.interviewassistant.data.di

import com.borisphen.interviewassistant.data.InterviewRepositoryImpl
import com.borisphen.interviewassistant.data.service.GroqApiService
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
        fun provideRepository(service: GroqApiService): InterviewRepositoryImpl {
            return InterviewRepositoryImpl(service)
        }

        @Provides
        @Singleton
        fun provideUseCase(repository: InterviewRepository): ProcessInterviewUseCase {
            return ProcessInterviewUseCase(repository)
        }
    }
}