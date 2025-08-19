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
import com.borisphen.core.data.sharedpreferences.PreferenceStorageImpl.Companion.KEY_RESULT_CODE
import com.borisphen.core.domain.ai.CreateNoteWithContextUseCase
import com.borisphen.core.domain.ocr.OcrEngine
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
    lateinit var saveScreenshotUseCase: SaveScreenshotUseCase

    @Inject
    lateinit var recognizer: RecognizerEngine

    @Inject
    @Named("Tess")
    lateinit var ocrEngine: OcrEngine

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    // Поток для ImageReader
    private var imageThread: HandlerThread? = null
    private var imageHandler: Handler? = null

    // Канал для последнего кадра (конфлуэнтный — держит только самый свежий)
    private val frameChannel = Channel<Bitmap>(capacity = 1)

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        super.onCreate()
        MemoryApplication.appComponent.inject(this)
        recognizer.setCallback { voiceText ->
            Log.d("ForegroundMemoryShotService", "Вопрос: $voiceText")
            serviceScope.launch {
                val bitmap = captureOneFrameOrNull()
                val ocrText = bitmap.takeIf { bitmap != null }?.let {
                    withContext(Dispatchers.Default) {
                        ocrEngine.process(it.toImageData())
                    }
                }
                Log.d("ForegroundMemoryShotService", "OCR Text: $ocrText")
                val screenshotPath =
                    bitmap?.let { saveScreenshotUseCase.saveExternal(it.toImageData()) }


                createNoteWithContextUseCase(
                    voiceText = voiceText,
                    ocrText = ocrText,
                    screenshotPath = screenshotPath
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
//        val data = intent.getParcelableExtra<Intent>(KEY_DATA_INTENT) ?: return START_NOT_STICKY

        val projectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = projectionManager.getMediaProjection(resultCode, dataIntent)

        initScreenCapture()
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

    private fun initScreenCapture() {
        // 1) Метрики экрана
        val bounds = getScreenBounds()
        val width = bounds.width()
        val height = bounds.height()
        val densityDpi = resources.displayMetrics.densityDpi

        // 2) Поток под ImageReader
        imageThread = HandlerThread("ScreenImageReader").apply { start() }
        imageHandler = Handler(imageThread!!.looper)

        // 3) ImageReader + постоянный слушатель (вешаем ДО createVirtualDisplay)
        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            /* maxImages = */ 3
        ).also { reader ->
            reader.setOnImageAvailableListener({ r ->
                try {
                    val img = r.acquireLatestImage() ?: return@setOnImageAvailableListener
                    val bmp = BitmapUtils.imageToBitmap(img)
                    img.close()
                    // Кладём свежий кадр, если канал не успел принять предыдущий — заменяем
                    frameChannel.trySend(bmp)
                } catch (t: Throwable) {
                    Log.e("ForegroundMemoryShotService", "Image error", t)
                }
            }, imageHandler)
        }

        // 4) VirtualDisplay
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface,
            null,
            null
        )

        Log.d(
            "ForegroundMemoryShotService",
            "VD created: ${virtualDisplay != null} ${width}x$height/$densityDpi"
        )
    }

    private suspend fun captureOneFrameOrNull(timeoutMs: Long = 3000L): Bitmap? =
        withTimeoutOrNull(timeoutMs) {
            // ждём ближайший свежий кадр
            frameChannel.receive()
        }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.stop()
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        serviceJob.cancel()

        imageReader = null
        virtualDisplay = null
        mediaProjection = null

        imageThread?.quitSafely()
        imageThread = null
        imageHandler = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        lateinit var dataIntent: Intent
    }
}
