package com.borisphen.memoryshot.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.borisphen.core.domain.ai.ProcessAiUseCase
import com.borisphen.core.domain.ai.Prompt
import com.borisphen.core.domain.note.SaveMemoryNoteUseCase
import com.borisphen.core.domain.note.model.MemoryNote
import com.borisphen.core.domain.speech.RecognizerEngine
import com.borisphen.core.domain.tags.GenerateTagsUseCase
import com.borisphen.memoryshot.MemoryApplication
import com.borisphen.memoryshot.util.ui.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForegroundMemoryShotService : Service() {

    @Inject
    lateinit var useCase: ProcessAiUseCase

    @Inject
    lateinit var generateTagsUseCase: GenerateTagsUseCase

    @Inject
    lateinit var recognizer: RecognizerEngine

    @Inject
    lateinit var saveMemoryNoteUseCase: SaveMemoryNoteUseCase

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        super.onCreate()
        MemoryApplication.appComponent.inject(this)
        recognizer.setCallback { text ->
            Log.d("ForegroundMemoryShotService", "Вопрос: $text")
            serviceScope.launch {
                useCase(text, Prompt.QUESTION_ANALYZER).fold(
                    ifRight = { answer ->
                        Log.d("ForegroundMemoryShotService", "Ответ: ${answer.answer}")
                        saveMemoryNoteUseCase(
                            MemoryNote(
                                title = "Голосовая заметка",
                                summary = answer.answer,
                                tags = listOf("интервью", "speech"),
                                originalText = text
                            )
                        )
                    },
                    ifLeft = {
                        Log.e("ForegroundMemoryShotService", "Ошибка обработки AI: $it")
                    }
                )
                delay(200)
                recognizer.start()
            }
        }
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode =
            intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED) ?: return START_NOT_STICKY
        val data = intent.getParcelableExtra<Intent>("data") ?: return START_NOT_STICKY

        val projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = projectionManager.getMediaProjection(resultCode, data)

        startScreenCapture()
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
        recognizer.start()
    }

    private fun startScreenCapture() {
        val handler = Handler(Looper.getMainLooper())
        val metrics = resources.displayMetrics
        imageReader = ImageReader.newInstance(
            metrics.widthPixels,
            metrics.heightPixels,
            PixelFormat.RGBA_8888,
            2
        )

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val bitmap = BitmapUtils.imageToBitmap(image)
            image.close()

            serviceScope.launch {
                val question = recognizer.getLastRecognizedText()
                val tags = generateTagsUseCase(question) // из domain
                saveMemoryNoteUseCase(
                    MemoryNote(
                        title = "Вопрос",
                        summary = question.take(100),
                        tags = tags,
                        originalText = question
                    )
                )
            }
        }, handler)
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.stop()
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
