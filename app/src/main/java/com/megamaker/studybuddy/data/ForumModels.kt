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
    val id: Int,
    val title: String,
    val content: String,
    val category: String?,
    val subjectId: Int,
    val facultyId: Int,
    val authorUserId: String?,
    val authorName: String?,
    val createdAt: String?,
    val repliesCount: Int,
    val replies: List<ForumReply> = emptyList()
)
