package com.borisphen.core.data.network.model

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)