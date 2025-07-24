package com.borisphen.core.domain.ai

import com.borisphen.util.Either

interface AiRepository {
    suspend fun processQuestion(question: String): Either<Throwable, AiResult>
}