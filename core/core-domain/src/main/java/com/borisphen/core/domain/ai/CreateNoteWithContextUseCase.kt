package com.borisphen.core.domain.ai

import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.core.domain.note.model.MemoryNote
import kotlinx.serialization.json.Json

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
                val (title, summary, tags) = parseJsonSafe(aiRes.answer, voiceText)
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

    /**
     * Попытка распарсить JSON от модели.
     * Если формат некорректный — делаем fallback к примитивному парсингу строк.
     */
    private fun parseJsonSafe(aiText: String, fallbackVoice: String): Triple<String, String, List<String>> {
        return try {
            val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<NoteAiResponse>(aiText)
            Triple(parsed.title, parsed.summary, parsed.tags.take(5))
        } catch (e: Exception) {
            // fallback: хотя бы что-то достанем
            Triple("Заметка", fallbackVoice.take(160), emptyList())
        }
    }
}
