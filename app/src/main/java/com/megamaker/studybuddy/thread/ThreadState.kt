package com.megamaker.studybuddy.thread

import com.megamaker.studybuddy.data.ForumReply
import com.megamaker.studybuddy.data.ForumThread

data class ThreadState(
    val baseUrl: String = "https://studdybuddyapp.azurewebsites.net",
    val apiKey: String = "SecretKey",
    val selectedThread: ForumThread = ForumThread(),
    val loading: Boolean = false,
    val error: String? = null,
    val selectedReplies: List<ForumReply> = emptyList(),
    val replyText: String = "",

    val name: String = "",
    val userId: String = "",
)
