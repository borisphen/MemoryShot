package com.borisphen.interviewassistant.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatRequest(
    @Json(name = "messages")
    val messages: List<Message>,
    @Json(name = "model")
    val model: String,
    @Json(name = "temperature")
    val temperature: Float,
    @Json(name = "max_tokens")
    val maxTokens: Int
)

data class Message(
    val role: String,
    val content: String
)