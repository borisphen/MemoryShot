package com.borisphen.memoryshot.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.borisphen.memoryshot.MemoryApplication
import com.borisphen.memoryshot.domain.ProcessAiUseCase
import com.borisphen.memoryshot.domain.speech.RecognizerEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForegroundMemoryShotService : Service() {

    @Inject
    lateinit var useCase: ProcessAiUseCase

    @Inject
    lateinit var recognizer: RecognizerEngine

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        super.onCreate()
        MemoryApplication.appComponent.inject(this)
        recognizer.setCallback { text ->
            Log.d("ForegroundMemoryShotService", "Вопрос: $text")
            serviceScope.launch {
                val result = useCase(text).fold(
                    ifRight = { answer ->
                        Log.d("ForegroundMemoryShotService", "Ответ: ${answer.answer}")
                        // TODO: Озвучить результат через TextToSpeech или AudioTrack
                    },
                    ifLeft = {
                        // TODO: Обработать ошибку
                    }
                )
                delay(200)
                recognizer.start()
            }
        }
        startForegroundService()
    }

    private fun startForegroundService() {
        val channelId = "interview_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Interview Service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Memory Shot")
            .setContentText("Listening for questions...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
        startForeground(1, notification)
        recognizer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.stop()
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
