package com.megamaker.studybuddy.thread

interface ThreadScreenEvent {

    data class OpenThreadScreen(val threadId: Int) : ThreadScreenEvent

}