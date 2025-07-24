package com.borisphen.memoryshot.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.borisphen.core.ui.theme.InterviewAssistantTheme
import com.borisphen.memoryshot.presentation.MainViewModel
import com.borisphen.memoryshot.presentation.di.AiComponent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RootContent(component: AiComponent) {

    val viewModel: MainViewModel =
        remember { component.viewModelFactory.create(component.useCase) }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.answerFlow.collectLatest { answer ->
            Log.d("InterviewApp", "Answer: $answer")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffectFlow.collect { effect ->
            Log.d("InterviewApp", "Effect: $effect")
            when (effect) {
                SideEffect.StartService -> viewModel.startService()
                SideEffect.StopService -> viewModel.stopService()
            }
        }
    }

    val onUiEvent: (UiEvent) -> Unit = {
        viewModel.onViewEvent(it)
    }

    InterviewAssistantTheme {
        Content(state = state, onEvent = onUiEvent)
    }
}

@Composable
private fun Content(
    state: AppState,
    onEvent: (UiEvent) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        MyButton(
            isServiceRunning = state.serviceIsRunning,
            onClick = { onEvent(UiEvent.ButtonClick) })
    }
}

@Composable
fun MyButton(isServiceRunning: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isServiceRunning) Color.Red else Color.Green)
        ) {
            Text(
                text = if (isServiceRunning) "Stop Service" else "Start Service",
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Content(AppState(), {})
}