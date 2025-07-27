package com.borisphen.memoryshot.history.presentation.content

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.borisphen.memoryshot.history.presentation.HistoryViewModel
import com.borisphen.memoryshot.history.presentation.model.MemoryNoteState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
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
            items(notes) { note ->

            }
        }
    }
}

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
            Row {
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