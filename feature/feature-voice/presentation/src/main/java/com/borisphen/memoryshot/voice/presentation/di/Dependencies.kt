package com.borisphen.memoryshot.voice.presentation.di

import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.core.domain.service.ServiceController
import com.borisphen.core.domain.speech.RecognizerEngine

interface Dependencies {
    val useCase: ProcessAiUseCase
    val recognizerEngine: RecognizerEngine
    val serviceController: ServiceController
}