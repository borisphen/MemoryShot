package com.borisphen.interviewassistant.service

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
import com.borisphen.interviewassistant.InterviewApplication
import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ForegroundInterviewService : Service() {

    lateinit var useCase: ProcessInterviewUseCase

    private val mainJob = SupervisorJob()
    private val ioJob = SupervisorJob()
    private val mainScope = CoroutineScope(mainJob + Dispatchers.Main.immediate)
    private val ioScope = CoroutineScope(ioJob + Dispatchers.IO)

    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreate() {
        super.onCreate()
        useCase = InterviewApplication.appComponent.useCase
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
                Log.d("InterviewService", "Вопрос: $question")
                if (!question.isNullOrBlank()) {
                    processQuestion(question)
                }
                startListening() // снова начинаем слушать
            }

            override fun onError(error: Int) {
                Log.e("InterviewService", "Speech recognition error: $error")
                startListening()
//                // Добавим небольшую паузу перед повторной активацией
//                mainScope.launch {
//                    delay(500) // 0.5 сек
//                    restartListening()
//                }
            }

            private fun restartListening() {
                try {
                    speechRecognizer?.stopListening()
                    speechRecognizer?.cancel()
                    startListening()
                } catch (e: Exception) {
                    Log.e("SpeechRecognizer", "Error restarting: ${e.message}", e)
                }
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
        ioScope.launch {
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
        ioJob.cancel()
        mainJob.cancel()
        speechRecognizer.destroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}