package com.borisphen.core.domain.screenshot

import com.borisphen.core.domain.ocr.ImageData
import com.borisphen.core.domain.ocr.OcrEngine

class ProcessScreenshotUseCase(
    private val saveScreenshotUseCase: SaveScreenshotUseCase,
    private val ocrEngine: OcrEngine
) {
    suspend operator fun invoke(imageData: ImageData): ProcessedScreenshot {
        val ocrText = ocrEngine.process(imageData)
        val screenshotPath = saveScreenshotUseCase.saveExternal(imageData)
        return ProcessedScreenshot(
            ocrText = ocrText,
            screenshotPath = screenshotPath
        )
    }
}