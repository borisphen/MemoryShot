package com.borisphen.memoryshot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.borisphen.memoryshot.ui.RootContent

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
//                startInterviewService()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val dependencies = MemoryApplication.appComponent
//        val component: AiComponent = AiComponent.factory().create(dependencies)

        setContent {
            RootContent(MemoryApplication.appComponent)
        }

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
//                startInterviewService()
            }

            shouldShowRequestPermissionRationale(permission) -> {
                // Здесь можно показать объяснение (optional)
                requestPermissionLauncher.launch(permission)
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
}

