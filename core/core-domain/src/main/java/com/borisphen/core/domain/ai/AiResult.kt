package com.borisphen.core.domain.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
data class AiResult(val answer: String)

@Serializable
data class NoteAiResponse(
    val title: String,
    val summary: String,
    val tags: List<String>
)