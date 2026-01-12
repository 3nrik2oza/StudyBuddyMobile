package com.megamaker.studybuddy.forum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.megamaker.studybuddy.data.ForumReply
import com.megamaker.studybuddy.data.ForumThread

@Composable
fun ForumScreen(state: ForumState, onEvent: (ForumEvent) -> Unit) {
    LaunchedEffect(Unit) { onEvent(ForumEvent.LoadThreads) }

    if (state.showCreateThreadDialog) {
        CreateThreadDialog(state, onEvent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (state.selectedThread == null) "Forum threads" else "Thread",
                style = MaterialTheme.typography.titleLarge
            )
            if (state.selectedThread == null) {
                Button(onClick = { onEvent(ForumEvent.ToggleCreateThreadDialog) }) { Text("New") }
            } else {
                OutlinedButton(onClick = { onEvent(ForumEvent.BackToList) }) { Text("Back") }
            }
        }

        if (state.loading) {
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))

        if (state.selectedThread == null) {
            ThreadList(
                threads = state.threads,
                onOpen = { onEvent(ForumEvent.OpenThread(it)) }
            )
        } else {
            ThreadDetails(
                thread = state.selectedThread,
                replies = state.selectedReplies,
                replyText = state.replyText,
                onReplyChange = { onEvent(ForumEvent.OnReplyTextChange(it)) },
                onSendReply = { onEvent(ForumEvent.SendReply) },
                onDelete = { onEvent(ForumEvent.DeleteThread(state.selectedThread.id)) }
            )
        }
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
private fun ThreadDetails(
    thread: ForumThread,
    replies: List<ForumReply>,
    replyText: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text(thread.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(thread.content, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDelete) { Text("Delete") }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text("Replies", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(replies) { r ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(r.authorName ?: "Unknown", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(r.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = replyText,
            onValueChange = onReplyChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Write reply") }
        )

        Spacer(Modifier.height(10.dp))

        Button(onClick = onSendReply, modifier = Modifier.fillMaxWidth()) {
            Text("Send reply")
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
                OutlinedTextField(
                    value = state.newThreadSubjectId.toString(),
                    onValueChange = { onEvent(ForumEvent.OnNewThreadSubjectIdChange(it.toIntOrNull() ?: 0)) },
                    label = { Text("SubjectId") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.newThreadFacultyId.toString(),
                    onValueChange = { onEvent(ForumEvent.OnNewThreadFacultyIdChange(it.toIntOrNull() ?: 0)) },
                    label = { Text("FacultyId") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
