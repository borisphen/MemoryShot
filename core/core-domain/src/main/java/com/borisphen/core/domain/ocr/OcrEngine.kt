package com.borisphen.core.domain.ocr

interface OcrEngine {
    suspend fun process(image: ImageData): String
}