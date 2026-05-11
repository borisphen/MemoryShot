package com.borisphen.core.data

import com.borisphen.core.data.model.NoteAiResponseDto
import com.borisphen.core.data.network.model.ChatRequest
import com.borisphen.core.data.network.model.Message
import com.borisphen.core.data.network.service.GroqApiService
import com.borisphen.core.domain.ai.AiRepository
import com.borisphen.core.domain.ai.NoteAiData
import com.borisphen.core.domain.ai.Prompt
import com.borisphen.core.domain.ai.Role
import com.borisphen.util.Either
import com.borisphen.util.apiCall
import com.squareup.moshi.Moshi

class AiRepositoryImpl(
    private val service: GroqApiService,
    private val moshi: Moshi
) : AiRepository {

    private val responseAdapter by lazy {
        moshi.adapter(NoteAiResponseDto::class.java)
    }

    override suspend fun processQuestion(
        question: String,
        prompt: Prompt
    ): Either<Throwable, NoteAiData> {
        val request = ChatRequest(
            model = MODEL,
            temperature = TEMPERATURE,
            maxTokens = MAX_TOKENS,
            messages = listOf(
                Message(Role.SYSTEM.value, promptMap[prompt].orEmpty()),
                Message(Role.USER.value, question)
            )
        )

        return apiCall { service.getChatCompletion(request) }.fold(
            ifLeft = { Either.Left(it) },
            ifRight = { chatResponse ->
                val rawJson = chatResponse.choices.firstOrNull()?.message?.content.orEmpty()
                Either.Right(parseResponse(rawJson, fallbackText = question))
            }
        )
    }

    private fun parseResponse(json: String, fallbackText: String): NoteAiData {
        return try {
            val dto = responseAdapter.fromJson(json)
                ?: return fallback(fallbackText)
            NoteAiData(
                title = dto.title,
                summary = dto.summary,
                tags = dto.tags.take(MAX_TAGS)
            )
        } catch (_: Exception) {
            fallback(fallbackText)
        }
    }

    private fun fallback(voiceText: String) = NoteAiData(
        title = "Заметка",
        summary = voiceText.take(MAX_SUMMARY_LENGTH),
        tags = emptyList()
    )

    companion object {
        const val MODEL = "llama-3.3-70b-versatile"
        const val TEMPERATURE = 0.2f
        const val MAX_TOKENS = 512
        private const val MAX_TAGS = 5
        private const val MAX_SUMMARY_LENGTH = 160

        private val promptMap = mapOf(
            Prompt.QUESTION_ANALYZER to QUESTION_ANALYZER_PROMPT,
            Prompt.TAG_GENERATOR to QUESTION_ANALYZER_PROMPT
        )

        private const val QUESTION_ANALYZER_PROMPT =
            "Ты — помощник. Из текста заметки сгенерируй JSON со структурой:\n" +
                    "{\n" +
                    "  \"title\": \"Краткий заголовок (без вводных слов)\",\n" +
                    "  \"summary\": \"Сжатое содержание мысли в первом лице или нейтрально\",\n" +
                    "  \"tags\": [\"тег1\", \"тег2\", \"тег3\"]\n" +
                    "}\n\n" +
                    "Правила:\n" +
                    "- Возвращай только JSON без пояснений и без форматирования в ``` .\n" +
                    "- Формулируй так, чтобы это выглядело как сжатая мысль пользователя, но без местоимений.\n" +
                    "- Игнорируй системные строки (например: \"Stop Service\", \"DEBUG\", \"pid\", " +
                    "время, логи и технические надписи).\n" +
                    "- Если есть голосовая заметка — именно она главная, приоритетнее OCR-текста.\n" +
                    "- OCR-текст учитывай только если в нём содержится полезная информация, а не мусор.\n" +
                    "- Не добавляй аналитики и комментариев, только переформулируй мысль.\n" +
                    "- Теги — массив строк, до 5 штук."
    }
}
