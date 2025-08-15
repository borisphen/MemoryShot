package com.borisphen.memoryshot.util.ui

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import java.nio.ByteBuffer
import androidx.core.graphics.createBitmap

object BitmapUtils {

    fun imageToBitmap(image: Image): Bitmap {
        val plane = image.planes[0]
        val buffer: ByteBuffer = plane.buffer
        val pixelStride = plane.pixelStride
        val rowStride = plane.rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = createBitmap(image.width + rowPadding / pixelStride, image.height)
        bitmap.copyPixelsFromBuffer(buffer)
        return Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
    }

    fun Bitmap.toImageData(): ImageData {
        val stream = java.io.ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return ImageData(
            bytes = stream.toByteArray(),
            width = width,
            height = height
        )
    }

    fun ByteArray.toBitmap(width: Int, height: Int): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }
}
