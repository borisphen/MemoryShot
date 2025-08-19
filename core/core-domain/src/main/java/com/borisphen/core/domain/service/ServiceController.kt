package com.borisphen.core.domain.service

interface ServiceController {
    fun startInterviewService(
        resultCode: Int,
        data: String,
    )

    fun stopInterviewService()
}