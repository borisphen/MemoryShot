package com.borisphen.memoryshot.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.borisphen.core.data.screenshot.ScreenCaptureManager
import com.borisphen.core.data.sharedpreferences.PreferenceStorageImpl.Companion.KEY_RESULT_CODE
import com.borisphen.core.domain.ai.CreateNoteWithContextUseCase
import com.borisphen.core.domain.ocr.OcrEngine
import com.borisphen.core.domain.screenshot.ProcessScreenshotUseCase
import com.borisphen.core.domain.screenshot.SaveScreenshotUseCase
import com.borisphen.core.domain.speech.RecognizerEngine
import com.borisphen.memoryshot.MemoryApplication
import com.borisphen.memoryshot.util.platform.BitmapUtils
import com.borisphen.memoryshot.util.platform.BitmapUtils.toImageData
import com.borisphen.memoryshot.util.platform.ScreenshotSaver
import com.borisphen.memoryshot.util.ui.getScreenBounds
import com.borisphen.util.getOrElse
import com.borisphen.util.right
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Named

class ForegroundMemoryShotService : Service() {

    @Inject
    lateinit var createNoteWithContextUseCase: CreateNoteWithContextUseCase
    @Inject
    lateinit var processScreenshotUseCase: ProcessScreenshotUseCase
    @Inject
    lateinit var recognizer: RecognizerEngine
    @Inject
    lateinit var screenCaptureManager: ScreenCaptureManager
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        super.onCreate()
        MemoryApplication.appComponent.inject(this)
        recognizer.setCallback { voiceText ->
            Log.d("ForegroundMemoryShotService", "Вопрос: $voiceText")
            serviceScope.launch {
                val bmp = screenCaptureManager.captureOneFrameOrNull()
                val processed =
                    if (bmp != null) processScreenshotUseCase(bmp.toImageData()) else null

                createNoteWithContextUseCase(
                    voiceText = voiceText,
                    ocrText = processed?.ocrText,
                    screenshotPath = processed?.screenshotPath
                )

                delay(200)
                recognizer.start()
            }
        }
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode =
            intent?.getIntExtra(KEY_RESULT_CODE, Activity.RESULT_CANCELED)
                ?: return START_NOT_STICKY
        if (dataIntent == null) return START_NOT_STICKY
//        val data = intent.getParcelableExtra<Intent>(KEY_DATA_INTENT) ?: return START_NOT_STICKY

        val projectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mediaProjection =
            projectionManager.getMediaProjection(resultCode, dataIntent!!) ?: return START_NOT_STICKY

        screenCaptureManager.start(mediaProjection)
        recognizer.start()

        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "interview_channel"
        val channel = NotificationChannel(
            channelId,
            "Interview Service",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Memory Shot")
            .setContentText("Listening for questions...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            recognizer.stop()
        } catch (_: Throwable) {
        }
        screenCaptureManager.stop()
        serviceJob.cancel()

    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        var dataIntent: Intent? = null
    }
}
