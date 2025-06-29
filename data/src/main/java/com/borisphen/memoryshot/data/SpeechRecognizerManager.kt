package com.borisphen.memoryshot.data

import android.content.Context

class SpeechRecognizerManager(private val context: Context) {
    private var listener: ((String) -> Unit)? = null

    fun setOnResultListener(listener: (String) -> Unit) {
        this.listener = listener
    }

    fun startListening() {
        // Инициализация Google SpeechRecognizer и передача текста в listener
    }
}