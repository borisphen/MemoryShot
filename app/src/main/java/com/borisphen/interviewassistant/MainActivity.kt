package com.borisphen.interviewassistant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.borisphen.presentation.ForegroundInterviewService
import com.borisphen.presentation.InterviewViewModel
import com.borisphen.presentation.di.InterviewComponent
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ContextCompat.startForegroundService(
            this,
            Intent(this, ForegroundInterviewService::class.java)
        )

        setContent {
            InterviewApp()
        }
    }
}

// InterviewApp.kt
@Composable
fun InterviewApp() {

    val component: InterviewComponent = remember { InterviewComponent.factory().create(InterviewApplication.appComponent) }

    val context = LocalContext.current

    val viewModel: InterviewViewModel = remember { component.viewModelFactory.create(component.useCase) }

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