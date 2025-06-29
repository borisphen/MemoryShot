package com.borisphen.memoryshot.presentation.di

import com.borisphen.memoryshot.domain.speech.RecognizerEngine
import com.borisphen.memoryshot.presentation.speech.AndroidSpeechRecognizerWrapper
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SpeechModule {
    @Binds
    @Singleton
    fun bindRecognizerEngine(impl: AndroidSpeechRecognizerWrapper): RecognizerEngine
}