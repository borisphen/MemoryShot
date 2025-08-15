package com.borisphen.core.domain.ai

import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.core.domain.note.model.MemoryNote

class CreateNoteWithContextUseCase(
    private val aiRepository: AiRepository,
    private val memoryNoteRepository: MemoryNoteRepository
) {
    /**
     * Объединяет голос + OCR, просит ИИ сгенерировать заголовок/резюме/теги, сохраняет единую заметку.
     */
    suspend operator fun invoke(
        voiceText: String,
        ocrText: String?,
        screenshotPath: String?
    ): Result<Unit> {
        val combined = buildString {
            appendLine("Голосовая заметка:")
            appendLine(voiceText)
            if (!ocrText.isNullOrBlank()) {
                appendLine()
                appendLine("Текст с экрана:")
                appendLine(ocrText)
            }
        }.trim()

        val ai = aiRepository.processQuestion(combined, Prompt.QUESTION_ANALYZER)
        return ai.fold(
            ifLeft = { Result.failure(it) },
            ifRight = { aiRes ->
                // простейший парсер: ожидаем блоки строк (можно улучшить при желании)
                val (title, summary, tags) = parseTitleSummaryTags(aiRes.answer)
                val note = MemoryNote(
                    title = title.ifBlank { "Заметка" },
                    summary = summary.ifBlank { voiceText.take(160) },
                    tags = tags,
                    originalText = voiceText,
                    ocrText = ocrText,
                    screenshotPath = screenshotPath
                )
                memoryNoteRepository.save(note)
                Result.success(Unit)
            }
        )
    }

    private fun parseTitleSummaryTags(aiText: String): Triple<String, String, List<String>> {
        // Очень простой хелпер. Можно заменить на более строгий формат/JSON
        val lines = aiText.lines().map { it.trim() }
        var title = ""
        var summary = ""
        val tags = mutableListOf<String>()

        lines.forEach { line ->
            when {
                line.startsWith("- Заголовок", true)
                        || line.startsWith("Заголовок", true) ->
                    title = line.substringAfter(':', "").trim()

                line.startsWith("- Краткое", true) || line.startsWith(
                    "Краткое",
                    true
                ) || line.startsWith("Резюме", true) ->
                    summary = line.substringAfter(':', "").trim()

                line.contains("Теги", true)
                        || line.contains("tags", true) ->
                    tags += line.substringAfter(':', "")
                        .split(',', ';').map { it.trim() }
                        .filter { it.isNotEmpty() }
            }
        }
        return Triple(title, summary, tags.take(5))
    }
}
