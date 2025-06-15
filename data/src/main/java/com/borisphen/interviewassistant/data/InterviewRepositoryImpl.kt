package com.borisphen.interviewassistant.data

import com.borisphen.interviewassistant.data.model.ChatRequest
import com.borisphen.interviewassistant.data.model.Message
import com.borisphen.interviewassistant.data.service.GroqApiService
import com.borisphen.interviewassistant.domain.InterviewRepository
import com.borisphen.interviewassistant.domain.InterviewResult

class InterviewRepositoryImpl(private val service: GroqApiService) : InterviewRepository {

    override suspend fun processQuestion(question: String): InterviewResult {
        val request = ChatRequest(
            model = MODEL,
            temperature = TEMPERATURE,
            maxTokens = MAX_TOKENS,
            messages = listOf(
                Message("system", "You are an expert Android interviewer."),
                Message("user", question)
            )
        )
        val response = service.getChatCompletion(request)
        return InterviewResult(
            response.choices.firstOrNull()?.message?.content ?: "Нет ответа от модели"
        )
    }

    companion object {
        const val MODEL: String = "mixtral-8x7b-32768"
        const val TEMPERATURE: Float = 0.7f
        const val MAX_TOKENS: Int = 512
    }
}