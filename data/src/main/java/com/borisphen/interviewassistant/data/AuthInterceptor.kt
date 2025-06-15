package com.borisphen.interviewassistant.data

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val key = "Bearer $apiKey"
        // TODO unsafe
        Log.d("Authorization", key)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", key)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}