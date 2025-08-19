package com.borisphen.core.data.ocr

import com.borisphen.core.domain.ocr.ImageData
import com.borisphen.core.domain.ocr.OcrEngine
import com.borisphen.memoryshot.util.platform.BitmapUtils.toBitmap
import com.borisphen.util.Either
import com.borisphen.util.left
import com.borisphen.util.right
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class OcrEngineImpl : OcrEngine {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun process(image: ImageData): String {
        val bitmap = image.toBitmap()
        return suspendCancellableCoroutine { cont ->
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { text -> cont.resume(text.text) }
                .addOnFailureListener { e -> cont.resume("") }
        }
    }
}