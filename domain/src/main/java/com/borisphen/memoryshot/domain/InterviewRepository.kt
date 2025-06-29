package com.borisphen.memoryshot.domain

interface InterviewRepository {
    suspend fun processQuestion(question: String): InterviewResult
}