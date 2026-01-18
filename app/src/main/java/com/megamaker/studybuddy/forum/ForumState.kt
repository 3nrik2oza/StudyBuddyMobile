package com.megamaker.studybuddy.forum

import com.megamaker.studybuddy.data.ForumReply
import com.megamaker.studybuddy.data.ForumThread
import com.megamaker.studybuddy.data.Subject


data class ForumState(
    val baseUrl: String = "https://studdybuddyapp.azurewebsites.net",
    val apiKey: String = "SecretKey",

    val loading: Boolean = false,
    val error: String? = null,

    val threads: List<ForumThread> = emptyList(),

    val selectedThread: ForumThread? = null,
    val selectedReplies: List<ForumReply> = emptyList(),

    val showCreateThreadDialog: Boolean = false,
    val newThreadTitle: String = "",
    val newThreadContent: String = "",
    val newThreadCategory: String = "Discussion",
    val newThreadSubjectId: Int = 0,
    val newThreadFacultyId: Int = 0,

    val replyText: String = "",
    val userId: String = "",
    val name: String = "",
    val listOfSubjects: List<Subject> = emptyList()
)
