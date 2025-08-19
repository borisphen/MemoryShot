package com.borisphen.memoryshot.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.borisphen.core.data.sharedpreferences.PreferenceStorageImpl.Companion.KEY_DATA_INTENT
import com.borisphen.core.data.sharedpreferences.PreferenceStorageImpl.Companion.KEY_RESULT_CODE
import com.borisphen.core.domain.service.ServiceController
import javax.inject.Inject

class ServiceControllerImpl @Inject constructor(
    private val context: Context,
) : ServiceController {

    override fun startInterviewService(
        resultCode: Int,
        data: String
    ) {
        val dataIntent = Intent.parseUri(data, 0)
        val intent = Intent(context, ForegroundMemoryShotService::class.java).apply {
            putExtra(KEY_RESULT_CODE, resultCode)
            putExtra(KEY_DATA_INTENT, dataIntent)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stopInterviewService() {
        val intent = Intent(context, ForegroundMemoryShotService::class.java)
        context.stopService(intent)
    }
}
