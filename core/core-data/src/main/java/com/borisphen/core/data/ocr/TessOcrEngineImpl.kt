package com.borisphen.core.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.borisphen.core.domain.ocr.ImageData
import com.borisphen.core.domain.ocr.OcrEngine
import com.borisphen.memoryshot.util.platform.BitmapUtils.toBitmap
import com.borisphen.util.Either
import com.borisphen.util.getOrThrow
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

class TessOcrEngineImpl(
    private val context: Context,
    private val defaultLanguages: String = "eng+rus"
) : OcrEngine {

    private val dataPath = "${context.filesDir.absolutePath}"
    private val languageDetector = LanguageIdentification.getClient()

    init {
        File("$dataPath/tessdata").mkdirs()
        initLanguageFiles()
    }

    override suspend fun process(imageData: ImageData): String? = withContext(Dispatchers.IO) {
        val bitmap = imageData.toBitmap()
        val language = detectLanguage(bitmap)
        recognizeText(bitmap, language)
    }

    private fun initLanguageFiles() {
        defaultLanguages.split("+").forEach { lang ->
            val langFile = File("$dataPath/tessdata/$lang.traineddata")
            if (!langFile.exists()) {
                context.assets.open("tessdata/$lang.traineddata").use { input ->
                    FileOutputStream(langFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun detectLanguage(bitmap: Bitmap): String {
        val roughText = recognizeText(bitmap, defaultLanguages)
        if (roughText == null) return "eng"
        return suspendCancellableCoroutine { cont ->
            languageDetector.identifyLanguage(roughText)
                .addOnSuccessListener { code ->
                    cont.resume(code.takeIf { it != "und" } ?: "eng")
                }
                .addOnFailureListener { cont.resume("eng") }
        }
    }

    private fun recognizeText(bitmap: Bitmap, language: String): String? {
        val tess = TessBaseAPI()
        return try {
            if (tess.init(dataPath, language)) {
                tess.setImage(bitmap)
                tess.utF8Text
            } else {
                null
            }
        } finally {
            tess.end() // Гарантированное освобождение ресурсов
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        return bitmap.copy(Bitmap.Config.ARGB_8888, true).apply {
            Canvas(this).drawBitmap(bitmap, 0f, 0f, Paint().apply {
                colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                    setSaturation(0f)
                    set(
                        floatArrayOf(
                            1.5f, 0f, 0f, 0f, -50f,
                            0f, 1.5f, 0f, 0f, -50f,
                            0f, 0f, 1.5f, 0f, -50f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                })
            })
        }
    }
}
