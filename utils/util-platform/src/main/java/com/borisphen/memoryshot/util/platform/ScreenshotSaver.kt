package com.borisphen.memoryshot.util.platform

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ScreenshotSaver(
    private val context: Context
) {

    suspend fun saveInternal(
        bitmap: Bitmap,
        namePrefix: String = FILE_NAME_PREFIX
    ): String {
        val dir = File(context.filesDir, SCREENS_DIR)
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, createFileName(namePrefix))
        FileOutputStream(file).use { out ->
            saveBitmap(bitmap, out)
        }
        return file.absolutePath
    }

    suspend fun saveExternal(
        bitmap: Bitmap,
        namePrefix: String = FILE_NAME_PREFIX
    ): String {
        val fileName = createFileName(namePrefix)

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/$EXTERNAL_DIR"
            )
        }

        val resolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { out ->
                saveBitmap(bitmap, out)
            }
        }

        return uri?.toString() ?: ""
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            // Внутренние
            val dir = File(context.filesDir, "screens")
            if (dir.exists()) dir.listFiles()?.forEach { it.delete() }

            // Внешние (через MediaStore)
            context.contentResolver.delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "${MediaStore.Images.Media.RELATIVE_PATH}=?",
                arrayOf(Environment.DIRECTORY_PICTURES + "/MemoryShot")
            )
        }
    }

    // --- приватные методы ---

    private fun createFileName(prefix: String): String =
        "$prefix${System.currentTimeMillis()}$FILE_EXTENSION"

    private fun saveBitmap(bitmap: Bitmap, out: OutputStream) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    fun deleteInternal(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.delete()
    }

    fun deleteExternal(uri: String): Boolean {
        return try {
            val resolver = context.contentResolver
            val deleted = resolver.delete(uri.toUri(), null, null)
            deleted > 0
        } catch (e: Exception) {
            Log.d("ScreenshotSaver", "Deleting $uri - FAILED: ${e.localizedMessage}")
            false
        }
    }

    companion object {
        const val SCREENS_DIR = "screens"
        const val EXTERNAL_DIR = "MemoryShot"
        const val FILE_NAME_PREFIX = "ms_shot_"
        const val FILE_EXTENSION = ".png"
        const val MIME_TYPE = "image/png"
    }
}
