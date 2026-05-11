package com.borisphen.core.domain.ai

/**
 * Результат обработки заметки нейросетью.
 * Чистая доменная модель — без зависимостей на сериализацию.
 */
data class NoteAiData(
    val title: String,
    val summary: String,
    val tags: List<String>
)
