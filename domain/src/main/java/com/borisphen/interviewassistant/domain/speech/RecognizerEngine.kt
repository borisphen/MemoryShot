package com.borisphen.interviewassistant.domain.speech

interface RecognizerEngine {
    fun start()
    fun stop()
    fun setCallback(callback: (String) -> Unit)
}