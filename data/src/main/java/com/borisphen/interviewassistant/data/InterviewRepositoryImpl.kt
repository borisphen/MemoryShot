package com.borisphen.interviewassistant.data

import com.borisphen.interviewassistant.domain.InterviewRepository
import com.borisphen.interviewassistant.domain.InterviewResult

class InterviewRepositoryImpl(private val api: NeuralNetworkApi) : InterviewRepository {
    override suspend fun processQuestion(question: String): InterviewResult {
        val response = api.ask(question)
        return InterviewResult(response)
    }
}