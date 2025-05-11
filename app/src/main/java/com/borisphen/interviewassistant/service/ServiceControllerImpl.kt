package com.borisphen.interviewassistant.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.borisphen.interviewassistant.domain.service.ServiceController
import javax.inject.Inject

class ServiceControllerImpl @Inject constructor(
    private val context: Context
) : ServiceController {

    override fun startInterviewService() {
        val intent = Intent(context, ForegroundInterviewService2::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopInterviewService() {
        val intent = Intent(context, ForegroundInterviewService2::class.java)
        context.stopService(intent)
    }
}