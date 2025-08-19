package com.borisphen.core.domain.screenshot

import com.borisphen.core.domain.ocr.ImageData

interface ScreenshotRepository {
    suspend fun saveInternal(imageData: ImageData): String
    suspend fun saveExternal(imageData: ImageData): String

    suspend fun deleteInternal(path: String): Boolean
    suspend fun deleteExternal(uri: String): Boolean

    suspend fun deleteAll()
}