package com.borisphen.core.domain.ai

class GenerateTagsUseCase(private val aiRepository: AiRepository) {
    suspend operator fun invoke(contextText: String): List<String> {
        val res = aiRepository.processQuestion(contextText, Prompt.TAG_GENERATOR)
        return res.fold(
            ifLeft = { emptyList() },
            ifRight = { ai -> 
                ai.answer
                    .split(',', '\n', ';')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .take(5)
            }
        )
    }
}