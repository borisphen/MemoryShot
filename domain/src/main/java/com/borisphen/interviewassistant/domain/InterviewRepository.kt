package com.borisphen.interviewassistant.domain

interface InterviewRepository {
    suspend fun processQuestion(question: String): InterviewResult
}