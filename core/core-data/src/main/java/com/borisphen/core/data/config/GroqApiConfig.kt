package com.borisphen.core.data.config

class GroqApiConfig(
    private val groqKey: String
) : ApiConfig {
    override val apiKey: String get() = groqKey
    override val baseUrl: String get() = "https://api.groq.com/openai/v1/"
}
