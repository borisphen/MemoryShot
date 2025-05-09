package com.borisphen.interviewassistant.data

interface NeuralNetworkApi {
    suspend fun ask(prompt: String): String
}