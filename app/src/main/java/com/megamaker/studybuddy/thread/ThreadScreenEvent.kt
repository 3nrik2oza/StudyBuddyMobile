package com.megamaker.studybuddy.thread

interface ThreadScreenEvent {

    data class OpenThreadScreen(val threadId: Int) : ThreadScreenEvent

    data class OnReplyChange(val reply: String) : ThreadScreenEvent

    object SendReply: ThreadScreenEvent

    object OnBackClick: ThreadScreenEvent

}