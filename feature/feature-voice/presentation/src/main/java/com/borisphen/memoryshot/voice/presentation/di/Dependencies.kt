package com.borisphen.memoryshot.voice.presentation.di

import com.borisphen.core.domain.service.ServiceController
import com.borisphen.core.domain.speech.RecognizerEngine

interface Dependencies {
    val recognizerEngine: RecognizerEngine
    val serviceController: ServiceController
}