package com.borisphen.core.domain.ai

import com.borisphen.core.domain.note.MemoryNoteRepository
import com.borisphen.core.domain.note.model.MemoryNote

class CreateNoteWithContextUseCase(
    private val aiRepository: AiRepository,
    private val memoryNoteRepository: MemoryNoteRepository
) {

    companion object {
        private const val MAX_SUMMARY_LENGTH = 160
    }
    /**
     * Объединяет голос + OCR, запрашивает у ИИ заголовок/резюме/теги,
     * сохраняет готовую заметку в репозиторий.
     */
    suspend operator fun invoke(
        voiceText: String,
        ocrText: String?,
        screenshotPath: String?
    ): Result<Unit> {
        val prompt = buildString {
            appendLine("Голосовая заметка:")
            appendLine(voiceText)
            if (!ocrText.isNullOrBlank()) {
                appendLine()
                appendLine("Текст с экрана:")
                appendLine(ocrText)
            }
        }.trim()

        return aiRepository.processQuestion(prompt, Prompt.QUESTION_ANALYZER).fold(
            ifLeft = { Result.failure(it) },
            ifRight = { aiData ->
                val note = MemoryNote(
                    title = aiData.title.ifBlank { "Заметка" },
                    summary = aiData.summary.ifBlank { voiceText.take(MAX_SUMMARY_LENGTH) },
                    tags = aiData.tags,
                    originalText = voiceText,
                    ocrText = ocrText,
                    screenshotPath = screenshotPath
                )
                memoryNoteRepository.save(note)
                Result.success(Unit)
            }
        )
    }
}
