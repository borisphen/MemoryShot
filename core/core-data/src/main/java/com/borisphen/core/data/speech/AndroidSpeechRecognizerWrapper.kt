package com.borisphen.core.data.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.borisphen.core.domain.speech.RecognizerEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("TooManyFunctions")
class AndroidSpeechRecognizerWrapper @Inject constructor(
    private val context: Context
) : RecognizerEngine, RecognitionListener {

    private var lastRecognizedText: String = ""
    private var recognizer: SpeechRecognizer? = null
    private var callback: (String) -> Unit = {}

    private val scope = CoroutineScope(Dispatchers.Main)
    private var restartJob: Job? = null

    private var errorCount = 0

    override fun setCallback(callback: (String) -> Unit) {
        this.callback = callback
    }

    override fun start() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w(TAG, "Speech recognition not available on this device")
            return
        }

        // Уничтожаем предыдущий экземпляр перед созданием нового
        destroyRecognizer()

        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(this@AndroidSpeechRecognizerWrapper)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        recognizer?.startListening(intent)
        Log.d(TAG, "Listening started")
    }

    override fun stop() {
        callback = {}
        destroyRecognizer()

        restartJob?.cancel()
        restartJob = null

        errorCount = 0
        Log.d(TAG, "Stopped")
    }

    override fun onResults(results: Bundle?) {
        errorCount = 0
        val text = results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
            ?: return

        Log.d(TAG, "Recognised: $text")
        lastRecognizedText = text

        scope.launch { callback(text) }
    }

    override fun onError(error: Int) {
        errorCount++
        Log.w(TAG, "Recognition error code=$error count=$errorCount")

        if (errorCount >= MAX_ERRORS) {
            Log.e(TAG, "Too many consecutive errors ($errorCount), stopping")
            errorCount = 0
            return
        }

        restartJob = scope.launch {
            delay(RESTART_DELAY_MS)
            start()
        }
    }

    override fun getLastRecognizedText(): String = lastRecognizedText

    // region RecognitionListener stubs

    override fun onReadyForSpeech(params: Bundle?) = Unit
    override fun onBeginningOfSpeech() = Unit
    override fun onRmsChanged(rmsdB: Float) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit
    override fun onEndOfSpeech() = Unit
    override fun onPartialResults(partialResults: Bundle?) = Unit
    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    // endregion

    private fun destroyRecognizer() {
        recognizer?.stopListening()
        recognizer?.destroy()
        recognizer = null
    }

    companion object {
        private const val TAG = "SpeechRecognizer"
        private const val RESTART_DELAY_MS = 500L
        private const val MAX_ERRORS = 5
    }
}
