package com.borisphen.memoryshot.presentation.di

import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.core.domain.speech.RecognizerEngine
import com.borisphen.memoryshot.domain.service.ServiceController

interface Dependencies {
    val useCase: ProcessAiUseCase
    val recognizerEngine: RecognizerEngine
    val serviceController: ServiceController
}