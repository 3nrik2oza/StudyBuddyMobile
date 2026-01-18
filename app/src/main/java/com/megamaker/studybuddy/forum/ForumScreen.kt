package com.megamaker.studybuddy.forum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.megamaker.studybuddy.data.ForumThread
import com.megamaker.studybuddy.data.Subject
import com.megamaker.studybuddy.ui.components.SimpleDropdown


@Composable
fun ForumScreen(state: ForumState, onEvent: (ForumEvent) -> Unit) {
    LaunchedEffect(Unit) { onEvent(ForumEvent.LoadThreads) }

    if (state.showCreateThreadDialog) {
        CreateThreadDialog(state, onEvent)
    }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (state.selectedThread == null) "Forum threads" else "Thread",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (state.loading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            state.error?.let {
                LazyColumn(

                ) {
                    item{
                        Spacer(Modifier.height(12.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }

                }

            }

            Spacer(Modifier.height(12.dp))

            if (state.selectedThread == null) {
                ThreadList(
                    threads = state.threads,
                    onOpen = { onEvent(ForumEvent.OpenThread(it)) }
                )
            }
        }

        Button(modifier = Modifier.padding(bottom = 50.dp, end = 30.dp), onClick = { onEvent(ForumEvent.ToggleCreateThreadDialog) }) { Text("New") }
    }
}

@Composable
private fun ThreadList(threads: List<ForumThread>, onOpen: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(threads) { t ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(t.id) }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(t.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(t.content, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "By ${t.authorName ?: "Unknown"} • replies: ${t.repliesCount}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}


@Composable
private fun CreateThreadDialog(state: ForumState, onEvent: (ForumEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(ForumEvent.ToggleCreateThreadDialog) },
        confirmButton = {
            Button(onClick = { onEvent(ForumEvent.CreateThread) }) { Text("Create") }
        },
        dismissButton = {
            OutlinedButton(onClick = { onEvent(ForumEvent.ToggleCreateThreadDialog) }) { Text("Cancel") }
        },
        title = { Text("New thread") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.newThreadTitle,
                    onValueChange = { onEvent(ForumEvent.OnNewThreadTitleChange(it)) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.newThreadContent,
                    onValueChange = { onEvent(ForumEvent.OnNewThreadContentChange(it)) },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = state.newThreadCategory,
                    onValueChange = { onEvent(ForumEvent.OnNewThreadCategoryChange(it)) },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                SimpleDropdown(
                    items = state.listOfSubjects,
                    onItemClick = { onEvent(ForumEvent.OnNewThreadSubjectIdChange(it)) }
                )
            }
        }
    )
}

