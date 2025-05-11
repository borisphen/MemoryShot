package com.borisphen.interviewassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.borisphen.presentation.InterviewViewModel
import com.borisphen.presentation.di.InterviewComponent
import com.borisphen.presentation.ui.RootContent
import kotlinx.coroutines.flow.collectLatest

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

        val dependencies = InterviewApplication.appComponent
        val component: InterviewComponent = InterviewComponent.factory().create(dependencies)

        setContent {
//            InterviewApp()
            RootContent(component)
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

// RootContent.kt
@Composable
fun InterviewApp() {

    val dependencies = InterviewApplication.appComponent
    val component: InterviewComponent =
        remember { InterviewComponent.factory().create(dependencies) }

    val viewModel: InterviewViewModel =
        remember { component.viewModelFactory.create(dependencies.useCase) }

    LaunchedEffect(Unit) {
        viewModel.answerFlow.collectLatest { answer ->
            Log.d("InterviewApp", "Answer: $answer")
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Interview Assistant", style = MaterialTheme.typography.headlineMedium)
        }
    }
}