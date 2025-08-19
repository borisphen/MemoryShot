package com.borisphen.memoryshot.util.platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import androidx.core.graphics.createBitmap
import com.borisphen.core.domain.ocr.ImageData
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

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
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return ImageData(
            bytes = stream.toByteArray(),
            width = width,
            height = height
        )
    }

//    fun ImageData.toBitmap(): Bitmap = with(this) {
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//    }

    fun ImageData.toBitmap(): Bitmap {
        require(bytes.isNotEmpty()) { "Byte array is empty" }

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also { bitmap ->
            if (bitmap.width != width || bitmap.height != height) {
                Log.w(
                    "ImageData",
                    "Size mismatch: expected ${width}x${height}, got ${bitmap.width}x${bitmap.height}"
                )
            }
        } ?: throw IllegalArgumentException("Failed to decode bitmap")
    }
}
