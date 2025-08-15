package com.borisphen.core.data.ocr

import com.borisphen.core.domain.ocr.ImageData
import com.borisphen.core.domain.ocr.OcrEngine
import com.borisphen.memoryshot.util.ui.BitmapUtils.toBitmap

class OcrEngineImpl(
    private val ocrLibrary: SomeOcrLibrary
) : OcrEngine {
    override suspend fun process(image: ImageData): String {
        val bitmap = image.bytes.toBitmap(image.width, image.height)
        return ocrLibrary.recognize(bitmap)
    }
}