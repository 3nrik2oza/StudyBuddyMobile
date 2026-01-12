package com.megamaker.studybuddy.forum

sealed class ForumEvent {
    data object LoadThreads : ForumEvent()
    data class OpenThread(val threadId: Int) : ForumEvent()
    data object BackToList : ForumEvent()

    data class OnReplyTextChange(val v: String) : ForumEvent()
    data object SendReply : ForumEvent()

    data class DeleteThread(val threadId: Int) : ForumEvent()

    data object ToggleCreateThreadDialog : ForumEvent()
    data class OnNewThreadTitleChange(val v: String) : ForumEvent()
    data class OnNewThreadContentChange(val v: String) : ForumEvent()
    data class OnNewThreadCategoryChange(val v: String) : ForumEvent()
    data class OnNewThreadSubjectIdChange(val v: Int) : ForumEvent()
    data class OnNewThreadFacultyIdChange(val v: Int) : ForumEvent()
    data object CreateThread : ForumEvent()
}
