package com.borisphen.core.domain.sharedpreferences

interface PreferenceStorage {

    suspend fun putValue(key: String, value: String)

    suspend fun putValue(key: String, value: Boolean)

    suspend fun getValue(key: String, defaultValue: String = ""): String

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean
}
