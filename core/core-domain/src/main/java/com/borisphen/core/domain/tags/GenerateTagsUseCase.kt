package com.borisphen.core.domain.tags

class GenerateTagsUseCase(private val aiTagGenerator: AiTagGenerator) {
    suspend operator fun invoke(contextText: String): List<String> {
        return aiTagGenerator.generateTags(contextText)
    }
}
