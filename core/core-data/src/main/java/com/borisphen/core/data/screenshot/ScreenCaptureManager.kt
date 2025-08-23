package com.borisphen.core.data.screenshot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.borisphen.memoryshot.util.platform.BitmapUtils
import com.borisphen.memoryshot.util.ui.getScreenBounds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/**
 * Отвечает ТОЛЬКО за:
 * - старт/стоп захвата
 * - выдачу единственного свежего кадра (Bitmap)
 */
class ScreenCaptureManager(
    private val appContext: Context
) {
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private var imageThread: HandlerThread? = null
    private var imageHandler: Handler? = null

    // Канал «последний кадр», чтобы читатель всегда получал самый свежий
    private val frameChannel = Channel<Bitmap>(capacity = 1)

    fun start(projection: MediaProjection) {
        if (mediaProjection != null) stop()

        mediaProjection = projection

        // 1) размер экрана
        val bounds = appContext.getScreenBounds()
        val width = bounds.width()
        val height = bounds.height()
        val densityDpi = appContext.resources.displayMetrics.densityDpi

        // 2) поток под imageReader
        imageThread = HandlerThread("ScreenCaptureReader").apply { start() }
        imageHandler = Handler(imageThread!!.looper)

        // 3) imageReader с постоянным слушателем
        imageReader = ImageReader.newInstance(
            width,
            height,
            PixelFormat.RGBA_8888,
            /*maxImages*/ 3
        ).also { reader ->
            reader.setOnImageAvailableListener({ r ->
                try {
                    val image = r.acquireLatestImage() ?: return@setOnImageAvailableListener
                    val bmp = BitmapUtils.imageToBitmap(image)
                    image.close()

                    // чистим старый bitmap перед записью нового
                    frameChannel.tryReceive().getOrNull()?.recycle()
                    frameChannel.trySend(bmp)
                } catch (t: Throwable) {
                    Log.e("ScreenCaptureManager", "Image error", t)
                }
            }, imageHandler)
        }

        // 4) virtual display
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width, height, densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader!!.surface, null, null
        )
        Log.d("ScreenCaptureManager", "VD created: ${virtualDisplay != null} ${width}x$height/$densityDpi")
    }

    /**
     * Вернуть ближайший свежий кадр или null по таймауту.
     */
    suspend fun captureOneFrameOrNull(timeoutMs: Long = 3000L): Bitmap? {
        return withTimeoutOrNull(timeoutMs) { frameChannel.receive() }
    }

    fun stop() {
        try {
            imageReader?.setOnImageAvailableListener(null, null)
            virtualDisplay?.release()
            imageReader?.close()
            mediaProjection?.stop()
        } catch (_: Throwable) { /* ignore */ }

        virtualDisplay = null
        imageReader = null
        mediaProjection = null

        imageThread?.quitSafely()
        imageThread?.join() // дожидаемся завершения
        imageThread = null
        imageHandler = null
    }
}
