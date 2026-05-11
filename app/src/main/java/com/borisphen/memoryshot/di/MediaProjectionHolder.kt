package com.borisphen.memoryshot.di

import android.app.Activity
import android.content.Intent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory хранилище данных MediaProjection.
 *
 * Intent от MediaProjectionManager содержит Binder-токен — объект живущий
 * только в памяти процесса. Сериализовать его в SharedPreferences невозможно,
 * поэтому держим в Singleton на время жизни процесса.
 */
@Singleton
class MediaProjectionHolder @Inject constructor() {

    var resultCode: Int = Activity.RESULT_CANCELED
        private set

    var intent: Intent? = null
        private set

    fun store(resultCode: Int, intent: Intent) {
        this.resultCode = resultCode
        this.intent = intent
    }

    fun isAvailable(): Boolean =
        intent != null && resultCode != Activity.RESULT_CANCELED
}
