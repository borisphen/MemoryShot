package com.borisphen.core.domain.ocr

import com.borisphen.util.Either

interface OcrEngine {
    suspend fun process(imageData: ImageData): String?
}