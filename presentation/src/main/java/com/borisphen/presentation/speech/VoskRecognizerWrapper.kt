package com.borisphen.presentation.speech

/*
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import com.borisphen.interviewassistant.domain.speech.RecognizerEngine
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.util.concurrent.Executors
import javax.inject.Inject

class VoskRecognizerWrapper @Inject constructor(
    private val context: Context
) : RecognizerEngine, RecognitionListener {

    private var onResult: (String) -> Unit = {}
    private lateinit var model: Model
    private lateinit var speechService: SpeechService

    override fun setCallback(callback: (String) -> Unit) {
        onResult = callback
    }

    override fun start() {
        Executors.newSingleThreadExecutor().execute {
            model = Model(context.filesDir.absolutePath + "/model-ru")
            val recognizer = Recognizer(model, 16000.0f)
            speechService = SpeechService(recognizer, 16000.0f)
            speechService.startListening(this)
        }
    }

    override fun stop() {
        if (::speechService.isInitialized) speechService.stop()
    }

    override fun onResult(hypothesis: String?) {
        hypothesis?.let {
            Handler(Looper.getMainLooper()).post {
                onResult(it)
            }
        }
    }

    override fun onError(e: Exception?) {
        Handler(Looper.getMainLooper()).postDelayed({
            start()
        }, 500)
    }

    override fun onTimeout() {
        Handler(Looper.getMainLooper()).postDelayed({
            start()
        }, 500)
    }

    override fun onPartialResult(hypothesis: String?) {}
    override fun onFinalResult(hypothesis: String?) {}
}*/
