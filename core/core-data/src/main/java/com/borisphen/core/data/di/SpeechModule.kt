package com.borisphen.core.data.di

import com.borisphen.core.data.speech.AndroidSpeechRecognizerWrapper
import com.borisphen.core.domain.speech.RecognizerEngine
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface SpeechModule {
    @Binds
    @Singleton
    fun bindRecognizerEngine(impl: AndroidSpeechRecognizerWrapper): RecognizerEngine
}