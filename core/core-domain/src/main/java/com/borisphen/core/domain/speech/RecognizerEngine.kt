package com.borisphen.core.domain.speech

interface RecognizerEngine {
    fun start()
    fun stop()
    fun setCallback(callback: (String) -> Unit)
    fun getLastRecognizedText(): String
}