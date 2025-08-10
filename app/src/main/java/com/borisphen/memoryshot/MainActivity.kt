package com.borisphen.memoryshot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.borisphen.core.ui.setEdgeToEdgeConfig
import com.borisphen.core.ui.theme.MemoryShotTheme
import com.borisphen.memoryshot.history.presentation.content.HistoryDependenciesProvider
import com.borisphen.memoryshot.history.presentation.content.HistoryScreen
import com.borisphen.memoryshot.ui.MainScreen
import kotlinx.serialization.Serializable

@Serializable
private data object ScreenA : NavKey

@Serializable
private data object ScreenB : NavKey

@Serializable
private data object ScreenC : NavKey

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
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)

//        val dependencies = MemoryApplication.appComponent
//        val component: AiComponent = AiComponent.factory().create(dependencies)

        setContent {

            val backStack = rememberNavBackStack(ScreenA)

            MemoryShotTheme {

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<ScreenA> {
//                        RootContent(MemoryApplication.appComponent)
                            MainScreen { backStack.add(ScreenB) }
                        }
                        entry<ScreenB> {
                            HistoryDependenciesProvider(MemoryApplication.appComponent) {
                                HistoryScreen {
                                    backStack.removeLastOrNull()
                                }
                            }
                        }
                    },
                    transitionSpec = {
                        // Slide in from right when navigating forward
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        // Slide in from left when navigating back
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    },
                    predictivePopTransitionSpec = {
                        // Slide in from left when navigating back
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    }
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onHistoryClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Главный экран") },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Default.DateRange, contentDescription = "История")
                    }
                }
            )
        }
    ) { padding ->
        MainScreen(MemoryApplication.appComponent, modifier = Modifier.padding(padding))
    }
}
