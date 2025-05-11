package com.borisphen.interviewassistant.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.borisphen.interviewassistant.InterviewApplication
import com.borisphen.interviewassistant.domain.ProcessInterviewUseCase
import com.borisphen.interviewassistant.domain.speech.RecognizerEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForegroundInterviewService2 : Service() {

    @Inject
    lateinit var useCase: ProcessInterviewUseCase

    @Inject
    lateinit var recognizer: RecognizerEngine

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        super.onCreate()
        InterviewApplication.appComponent.inject(this)
        recognizer.setCallback { text ->
            serviceScope.launch {
                val result = useCase(text)
                // TODO: Озвучить результат через TextToSpeech или AudioTrack
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
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Interview Assistant")
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
