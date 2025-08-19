package com.borisphen.core.domain.screenshot

import com.borisphen.core.domain.ocr.ImageData

class SaveScreenshotUseCase(
    private val repository: ScreenshotRepository
) {
    suspend fun saveInternal(imageData: ImageData): String =
        repository.saveInternal(imageData)

    suspend fun saveExternal(imageData: ImageData): String =
        repository.saveExternal(imageData)

    suspend fun deleteInternal(path: String): Boolean =
        repository.deleteInternal(path)

    suspend fun deleteExternal(uri: String): Boolean =
        repository.deleteExternal(uri)

    suspend fun clearAll() = repository.deleteAll()
}