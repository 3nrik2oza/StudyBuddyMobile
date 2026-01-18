package com.megamaker.studybuddy.thread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.megamaker.studybuddy.data.ForumReply
import com.megamaker.studybuddy.data.ForumThread
import com.megamaker.studybuddy.forum.ForumEvent

@Composable
fun ThreadScreen(
    state: ThreadState,
    onEvent: (ThreadScreenEvent) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 20.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                onClick = { onEvent(ThreadScreenEvent.OnBackClick) },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = "Thread",
                style = MaterialTheme.typography.titleLarge
            )
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

        ThreadDetails(
            thread = state.selectedThread,
            replies = state.selectedReplies,
            replyText = state.replyText,
            onReplyChange = { onEvent(ThreadScreenEvent.OnReplyChange(it)) },
            onSendReply = { onEvent(ThreadScreenEvent.SendReply) },
        )
    }
}


@Composable
private fun ThreadDetails(
    thread: ForumThread,
    replies: List<ForumReply>,
    replyText: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text(thread.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(6.dp))
                Text(thread.content, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp))

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