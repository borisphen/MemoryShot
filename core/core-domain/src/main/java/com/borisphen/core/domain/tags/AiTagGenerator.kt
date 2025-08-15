package com.borisphen.core.domain.tags

interface AiTagGenerator {
    suspend fun generateTags(text: String): List<String>
}