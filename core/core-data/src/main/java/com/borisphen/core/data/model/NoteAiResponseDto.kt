package com.borisphen.core.data.model

import com.squareup.moshi.JsonClass

/**
 * DTO для парсинга JSON-ответа от LLM.
 * Внутренний класс data-слоя — не выходит за его пределы.
 */
@JsonClass(generateAdapter = true)
internal data class NoteAiResponseDto(
    val title: String,
    val summary: String,
    val tags: List<String>
)
