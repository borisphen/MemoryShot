package com.borisphen.core.data.screenshot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.borisphen.memoryshot.util.platform.BitmapUtils
import com.borisphen.memoryshot.util.platform.getScreenBounds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ScreenCaptureManager(
    private val appContext: Context
) {

    companion object {
        private const val MAX_IMAGES = 3
    }
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private var imageThread: HandlerThread? = null
    private var imageHandler: Handler? = null

    // Гонка защиты: true — принимаем кадры; false — игнорируем (стоп/не запущено)
    private val capturing = AtomicBoolean(false)

    // Защита на время конвертации image->bitmap и доступа к imageReader.
    private val convertLock = ReentrantLock()

    // Конфлуэнтный канал: держит только последний кадр.
    // Если кадр не забрали — при замене старый утилизируем.
    private val frameChannel = Channel<Bitmap>(
        capacity = Channel.CONFLATED,
        onUndeliveredElement = { bmp -> try { bmp.recycle() } catch (_: Throwable) {} }
    )

    @Suppress("TooGenericExceptionCaught")
    fun start(projection: MediaProjection) {
        // Идемпотентно: если уже запущено — сперва стоп
        stop()

        mediaProjection = projection

        val bounds = appContext.getScreenBounds()
        val width = bounds.width()
        val height = bounds.height()
        val densityDpi = appContext.resources.displayMetrics.densityDpi

        imageThread = HandlerThread("ScreenCaptureReader").apply { start() }
        imageHandler = Handler(imageThread!!.looper)

        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            MAX_IMAGES
        ).also { reader ->
            reader.setOnImageAvailableListener({ r ->
                // Быстрый предохранитель: если нас уже стопят — не трогаем image.
                if (!capturing.get()) {
                    // Даже если пришло — сразу закрываем, чтобы не держать ресурсы
                    try { r.acquireLatestImage()?.close() } catch (_: Throwable) {}
                    return@setOnImageAvailableListener
                }

                var image: Image? = null
                try {
                    image = r.acquireLatestImage()
                    if (image == null) return@setOnImageAvailableListener

                    // На время конвертации блокируемся, чтобы stop() не освободил ресурсы.
                    convertLock.withLock {
                        if (!capturing.get()) {
                            // На всякий случай: если выключили прямо перед lock
                            return@withLock
                        }
                        val bmp = BitmapUtils.imageToBitmap(image)
                        if (bmp != null) {
                            // Заменим старый кадр на новый (старый будет утилизирован onUndeliveredElement)
                            frameChannel.trySend(bmp)
                        }
                    }
                } catch (t: Throwable) {
                    Log.e("ScreenCaptureManager", "Image error", t)
                } finally {
                    try { image?.close() } catch (_: Throwable) {}
                }
            }, imageHandler)
        }

        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width, height, densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface, null, null
        )

        capturing.set(true)
        Log.d("ScreenCaptureManager", "VD created: ${virtualDisplay != null} ${width}x$height/$densityDpi")
    }

    suspend fun captureOneFrameOrNull(timeoutMs: Long = 3000L): Bitmap? {
        return withTimeoutOrNull(timeoutMs) { frameChannel.receive() }
    }

    fun stop() {
        // Отключаем приём кадров максимально рано
        capturing.set(false)

        // Снимаем листенер: больше колбэков не будет отправлено на handler
        try {
            imageReader?.setOnImageAvailableListener(null, null)
        } catch (_: Throwable) {}

        // Дожидаемся, пока возможная текущая конвертация (внутри listener) завершится
        convertLock.withLock {
            // Внутри замка безопасно освобождаем всё
            try { virtualDisplay?.release() } catch (_: Throwable) {}
            try { imageReader?.close() } catch (_: Throwable) {}
            try { mediaProjection?.stop() } catch (_: Throwable) {}
            virtualDisplay = null
            imageReader = null
            mediaProjection = null
        }

        // Корректно выключаем поток листенера
        try {
            imageThread?.quitSafely()
            imageThread?.join()
        } catch (_: Throwable) {}
        imageThread = null
        imageHandler = null

        // Дреним и утилизируем последний кадр (если остался)
        try {
            val leftover = frameChannel.tryReceive().getOrNull()
            leftover?.recycle()
        } catch (_: Throwable) {}
    }
}
