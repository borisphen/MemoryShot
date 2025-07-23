package com.borisphen.memoryshot.data

import com.borisphen.memoryshot.data.model.ChatRequest
import com.borisphen.memoryshot.data.model.Message
import com.borisphen.memoryshot.data.service.GroqApiService
import com.borisphen.memoryshot.domain.AiRepository
import com.borisphen.memoryshot.domain.AiResult
import com.borisphen.util.Either
import com.borisphen.util.apiCall
import com.borisphen.util.right

class AiRepositoryImpl(private val service: GroqApiService) : AiRepository {

    override suspend fun processQuestion(question: String): Either<Throwable, AiResult> {
        val request = ChatRequest(
            model = MODEL,
            temperature = TEMPERATURE,
            maxTokens = MAX_TOKENS,
            messages = listOf(
                Message(
                    "system", "Ты — помощник. Из текста заметки сгенерируй:\n" +
                            "- Заголовок\n" +
                            "- Краткое резюме\n" +
                            "- Тематические теги (до 5)"
                ),
                Message("user", question)
            )
        )
        return apiCall { service.getChatCompletion(request) }.fold(
            ifLeft = { error ->
                Either.Left(error)
            },
            ifRight = { chatResponse ->
                val result = AiResult(
                    chatResponse.choices.firstOrNull()?.message?.content ?: "Нет ответа от модели"
                )
                result.right()
            }
        )
    }

    companion object {
        const val MODEL: String = "mixtral-8x7b-32768"
        const val TEMPERATURE: Float = 0.7f
        const val MAX_TOKENS: Int = 512
    }
}