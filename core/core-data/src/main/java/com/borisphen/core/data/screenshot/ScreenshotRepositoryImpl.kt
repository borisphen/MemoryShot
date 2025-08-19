package com.borisphen.core.data.screenshot

import com.borisphen.core.domain.ocr.ImageData
import com.borisphen.core.domain.screenshot.ScreenshotRepository
import com.borisphen.memoryshot.util.platform.BitmapUtils.toBitmap
import com.borisphen.memoryshot.util.platform.ScreenshotSaver

class ScreenshotRepositoryImpl(
    private val saver: ScreenshotSaver
) : ScreenshotRepository {

    override suspend fun saveInternal(imageData: ImageData): String =
        saver.saveInternal(imageData.toBitmap())

    override suspend fun saveExternal(imageData: ImageData): String =
        saver.saveExternal(imageData.toBitmap())

    override suspend fun deleteInternal(path: String): Boolean = saver.deleteInternal(path)

    override suspend fun deleteExternal(uri: String): Boolean = saver.deleteExternal(uri)

    override suspend fun deleteAll() = saver.deleteAll()
}