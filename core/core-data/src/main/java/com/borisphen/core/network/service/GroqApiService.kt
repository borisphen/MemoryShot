package com.borisphen.core.network.service

import com.borisphen.core.network.model.ChatRequest
import com.borisphen.core.network.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequest): Response<ChatResponse>
}