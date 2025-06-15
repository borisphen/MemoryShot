package com.borisphen.interviewassistant.config

import com.borisphen.interviewassistant.BuildConfig
import com.borisphen.interviewassistant.data.config.ApiConfig

class GroqApiConfig() : ApiConfig {
    override val apiKey: String
        get() = BuildConfig.GROQ_API_KEY
}