package com.borisphen.presentation.di

import com.borisphen.interviewassistant.domain.speech.RecognizerEngine
import com.borisphen.presentation.speech.AndroidSpeechRecognizerWrapper
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SpeechModule {
    @Binds
    @Singleton
    fun bindRecognizerEngine(impl: AndroidSpeechRecognizerWrapper): RecognizerEngine
}