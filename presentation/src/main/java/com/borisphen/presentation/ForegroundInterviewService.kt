package com.borisphen.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import com.borisphen.presentation.di.InterviewComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForegroundInterviewService : Service() {

    @Inject
    lateinit var useCase: ProcessInterviewUseCase

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate() {
        super.onCreate()
        (applicationContext as InterviewComponent).inject(this)
        startForegroundServiceWithNotification()
        setupSpeechRecognizer()
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "interview_assistant_channel"
        val channelName = "Interview Assistant Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Ассистент собеседований")
            .setContentText("Слушаю вопрос...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()

        startForeground(1, notification)
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val question = matches?.firstOrNull()
                if (!question.isNullOrBlank()) {
                    processQuestion(question)
                }
                startListening() // снова начинаем слушать
            }

            override fun onError(error: Int) {
                Log.e("InterviewService", "Speech recognition error: $error")
                startListening()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        startListening()
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
        }
        speechRecognizer.startListening(intent)
    }

    private fun processQuestion(question: String) {
        serviceScope.launch {
            try {
                val result = useCase(question)
                speak(result.answer)
            } catch (e: Exception) {
                Log.e("InterviewService", "Error processing question", e)
            }
        }
    }

    private fun speak(answer: String) {
        // Тут можно использовать TextToSpeech или отправку в BT-наушники, пока просто лог
        Log.d("InterviewService", "Ответ: $answer")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        speechRecognizer.destroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}