package com.borisphen.memoryshot.config

import com.borisphen.memoryshot.BuildConfig
import com.borisphen.memoryshot.data.config.ApiConfig

class GroqApiConfig : ApiConfig {
    override val apiKey: String
        get() = BuildConfig.GROQ_API_KEY
}

