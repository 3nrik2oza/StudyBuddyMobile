package com.megamaker.studybuddy.data

data class ForumReply(
    val id: Int,
    val forumThreadId: Int,
    val forumThread: String?,
    val authorUserId: String?,
    val authorName: String?,
    val content: String,
    val createdAt: String?
)

data class ForumThread(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val category: String? = null,
    val subjectId: Int = 0,
    val facultyId: Int = 0,
    val authorUserId: String? = null,
    val authorName: String? = null,
    val createdAt: String? = null,
    val repliesCount: Int = 0,
    val replies: List<ForumReply> = emptyList()
)
