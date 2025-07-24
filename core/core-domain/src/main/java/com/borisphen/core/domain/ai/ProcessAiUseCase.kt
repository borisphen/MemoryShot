package com.borisphen.core.domain.ai

import com.borisphen.util.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProcessAiUseCase(private val repository: AiRepository) {
    suspend operator fun invoke(question: String): Either<Throwable, AiResult> =
        withContext(Dispatchers.IO) {
            repository.processQuestion(question)
        }
}