package com.borisphen.core.data.tags

import com.borisphen.core.data.config.GroqApiConfig
import com.borisphen.core.domain.tags.AiTagGenerator

class AiTagGeneratorImpl(
    private val groqApi: GroqApiConfig
) : AiTagGenerator {
    override suspend fun generateTags(text: String): List<String> {
        val response = groqApi.getTags(text) // Запрос в Groq
        return response.tags
    }
}