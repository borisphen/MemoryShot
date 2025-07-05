package com.borisphen.memoryshot.domain

import com.borisphen.util.Either

interface AiRepository {
    suspend fun processQuestion(question: String): Either<Throwable, AiResult>
}