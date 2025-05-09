package com.borisphen.interviewassistant.domain

class ProcessInterviewUseCase(private val repository: InterviewRepository) {
    suspend operator fun invoke(question: String): InterviewResult {
        return repository.processQuestion(question)
    }
}