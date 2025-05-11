package com.borisphen.presentation.di

import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import com.borisphen.interviewassistant.domain.service.ServiceController
import com.borisphen.interviewassistant.domain.speech.RecognizerEngine

interface Dependencies {
    val useCase: ProcessInterviewUseCase
    val recognizerEngine: RecognizerEngine
    val serviceController: ServiceController
}