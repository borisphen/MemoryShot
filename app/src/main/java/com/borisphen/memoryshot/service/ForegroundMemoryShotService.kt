package com.borisphen.memoryshot.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.borisphen.core.data.screenshot.ScreenCaptureManager
import com.borisphen.core.domain.ai.CreateNoteWithContextUseCase
import com.borisphen.core.domain.screenshot.ProcessScreenshotUseCase
import com.borisphen.core.domain.speech.RecognizerEngine
import com.borisphen.memoryshot.MemoryApplication
import com.borisphen.memoryshot.di.MediaProjectionHolder
import com.borisphen.memoryshot.util.platform.BitmapUtils.toImageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForegroundMemoryShotService : Service() {

    // region Injected dependencies

    @Inject
    lateinit var createNoteWithContextUseCase: CreateNoteWithContextUseCase

    @Inject
    lateinit var processScreenshotUseCase: ProcessScreenshotUseCase

    @Inject
    lateinit var recognizer: RecognizerEngine

    @Inject
    lateinit var screenCaptureManager: ScreenCaptureManager

    @Inject
    lateinit var projectionHolder: MediaProjectionHolder

    // endregion

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // region Service lifecycle

    override fun onCreate() {
        super.onCreate()
        MemoryApplication.appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> onActionStart()
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCapture()
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // endregion

    // region Action handlers

    private fun onActionStart() {
        showForegroundNotification()
        startCapture()
    }

    // endregion

    // region Capture logic

    private fun startCapture() {
        if (!projectionHolder.isAvailable()) {
            Log.e(TAG, "MediaProjection data not available — permission not granted")
            return
        }

        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mediaProjection = projectionManager.getMediaProjection(
            projectionHolder.resultCode,
            projectionHolder.intent!!
        ) ?: run {
            Log.e(TAG, "Failed to get MediaProjection")
            return
        }

        screenCaptureManager.start(mediaProjection)
        startListening()
    }

    private fun startListening() {
        recognizer.setCallback(::onVoiceResult)
        recognizer.start()
    }

    private fun onVoiceResult(voiceText: String) {
        Log.d(TAG, "Voice input received: $voiceText")
        serviceScope.launch {
            val bitmap = screenCaptureManager.captureOneFrameOrNull()
            val screenshot = bitmap?.let { processScreenshotUseCase(it.toImageData()) }

            createNoteWithContextUseCase(
                voiceText = voiceText,
                ocrText = screenshot?.ocrText,
                screenshotPath = screenshot?.screenshotPath
            )

            delay(RECOGNITION_RESTART_DELAY_MS)
            recognizer.start()
        }
    }

    private fun stopCapture() {
        runCatching { recognizer.stop() }
        screenCaptureManager.stop()
    }

    // endregion

    // region Notification

    private fun showForegroundNotification() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "MemoryShot Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MemoryShot")
            .setContentText("Listening...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    // endregion

    companion object {
        const val ACTION_START = "com.borisphen.memoryshot.START"
        private const val ACTION_STOP = "com.borisphen.memoryshot.STOP"

        private const val CHANNEL_ID = "memory_shot_channel"
        private const val NOTIFICATION_ID = 101
        private const val RECOGNITION_RESTART_DELAY_MS = 200L
        private const val TAG = "MemoryShotService"
    }
}
