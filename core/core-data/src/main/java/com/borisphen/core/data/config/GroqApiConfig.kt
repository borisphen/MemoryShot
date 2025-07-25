package com.borisphen.core.data.config

import com.borisphen.core.domain.config.ApiConfig

class GroqApiConfig(
    private val groqKey: String
) : ApiConfig {
    override val apiKey: String get() = groqKey
    override val baseUrl: String get() = "https://api.groq.com/openai/v1/"
}
