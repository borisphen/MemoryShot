package com.borisphen.memoryshot.presentation.di

import com.borisphen.memoryshot.domain.ProcessInterviewUseCase
import com.borisphen.memoryshot.domain.service.ServiceController
import com.borisphen.memoryshot.domain.speech.RecognizerEngine

interface Dependencies {
    val useCase: ProcessInterviewUseCase
    val recognizerEngine: RecognizerEngine
    val serviceController: ServiceController
}