package com.borisphen.core.data.network.service

import com.borisphen.core.data.network.model.ChatRequest
import com.borisphen.core.data.network.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GroqApiService {
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequest): Response<ChatResponse>
}