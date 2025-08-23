package com.borisphen.memoryshot.history.presentation.content

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.borisphen.core.ui.theme.MemoryShotTheme
import com.borisphen.memoryshot.history.presentation.HistoryViewModel
import com.borisphen.memoryshot.history.presentation.di.DaggerHistoryComponent
import com.borisphen.memoryshot.history.presentation.di.HistoryDependencies
import com.borisphen.memoryshot.history.presentation.model.MemoryNoteState
import com.borisphen.memoryshot.util.ui.composeViewModel

val LocalHistoryDependencies =
    compositionLocalOf<HistoryDependencies> { error("No depencencies found!") }

@Composable()
fun HistoryScreenEntry(
    dependencies: HistoryDependencies,
    onBackClick: () -> Unit
) {
    val component = remember {
        DaggerHistoryComponent.factory().create(dependencies)
    }
    val viewModel: HistoryViewModel = composeViewModel {
        component.viewModelFactory.create()
    }
    HistoryScreen(viewModel, onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История заметок") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(notes, key = { it.id }) { note ->
                MemoryNoteItem(note = note) { }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MemoryNoteItem(
    note: MemoryNoteState,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = note.summary, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = Int.MAX_VALUE
                ) {
                    note.tags.forEach {
                        AssistChip(
                            onClick = {},
                            label = { Text(it) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    name = "Memory Note Item - Light"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Memory Note Item - Dark"
)
@Composable
private fun MemoryNoteItemPreview() {
    MemoryShotTheme {
        MemoryNoteItem(
            note = MemoryNoteState(
                id = 1L,
                title = "Пример заметки",
                summary = "Это краткое описание заметки для превью",
                tags = listOf("Android", "Kotlin", "Compose", "Java", "Gutten Morgen"),
                originalText = "Полный текст заметки, который хранится отдельно",
                createdAt = System.currentTimeMillis()
            ),
            onDelete = {}
        )
    }
}

@Suppress("FunctionNaming")
@Composable
fun HistoryDependenciesProvider(
    historyDependencies: HistoryDependencies,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalHistoryDependencies provides historyDependencies) {
        content()
    }
}