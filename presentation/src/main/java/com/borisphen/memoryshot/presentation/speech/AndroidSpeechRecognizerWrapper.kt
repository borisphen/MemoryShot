package com.borisphen.memoryshot.presentation.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.borisphen.memoryshot.domain.speech.RecognizerEngine
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class AndroidSpeechRecognizerWrapper @Inject constructor(
    private val context: Context
) : RecognizerEngine, RecognitionListener {

    private var recognizer: SpeechRecognizer? = null
    private var callback: (String) -> Unit = {}

    private val mainScope = MainScope()

    override fun setCallback(callback: (String) -> Unit) {
        this.callback = callback
    }

    override fun start() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@AndroidSpeechRecognizerWrapper)
            }
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")

                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            recognizer?.startListening(intent)
        }
    }

    override fun stop() {
        recognizer?.stopListening()
        recognizer?.destroy()
        mainScope.cancel()
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val result = matches?.firstOrNull() ?: return
        mainScope.launch {
            callback(result)
        }
    }

    override fun onError(error: Int) {
        // Попробуем перезапустить после небольшой задержки
        mainScope.launch {
            delay(500)
            start()
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}