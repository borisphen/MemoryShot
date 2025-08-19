package com.borisphen.core.data.sharedpreferences

import android.content.SharedPreferences
import com.borisphen.core.domain.sharedpreferences.PreferenceStorage

class PreferenceStorageImpl(
    private val sharedPreferences: SharedPreferences,
) : PreferenceStorage {

    override suspend fun putValue(key: String, value: String) {
        sharedPreferences[key] = value
    }

    override suspend fun putValue(key: String, value: Boolean) {
        sharedPreferences[key] = value
    }

    override suspend fun getValue(key: String, defaultValue: String): String {
        return sharedPreferences[key, defaultValue]
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences[key, defaultValue]
    }

    companion object {
        const val KEY_RESULT_CODE = "resultCode"
        const val KEY_DATA_INTENT = "dataIntent"
    }
}
