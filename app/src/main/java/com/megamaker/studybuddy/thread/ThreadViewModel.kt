package com.megamaker.studybuddy.thread

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.megamaker.studybuddy.data.AuthStore
import com.megamaker.studybuddy.data.ForumReply
import com.megamaker.studybuddy.data.ForumThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ThreadViewModel(application: Application): AndroidViewModel(application){



    private val _state = MutableStateFlow(ThreadState())
    val state = _state.asStateFlow()

    private val queue: RequestQueue = Volley.newRequestQueue(application)

    private val authStore = AuthStore(application)


    init {
        viewModelScope.launch {
            val name = authStore.nameFlow.first() ?: ""
            val id = authStore.idFlow.first() ?: ""
            _state.update { it.copy(name = name, userId = id, ) }
        }
    }

    fun onEvent(event: ThreadScreenEvent){
        when(event){
            is ThreadScreenEvent.OpenThreadScreen -> loadThread(event.threadId)
            is ThreadScreenEvent.SendReply -> sendReply()
            is ThreadScreenEvent.OnReplyChange -> _state.update { it.copy(replyText = event.reply) }
        }

    }

    private fun loadThread(id: Int) {
        _state.update { it.copy(loading = true, error = null) }

        val url = "${_state.value.baseUrl}/api/v1/ForumThread/$id"
        val req = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { resp ->
                try {
                    val thread = parseThread(resp)
                    val replies = thread.replies
                    _state.update { it.copy(loading = false, selectedThread = thread, selectedReplies = replies) }
                } catch (e: Exception) {
                    _state.update { it.copy(loading = false, error = e.toString()) }
                }
            },
            { err -> _state.update { it.copy(loading = false, error = err.toString()) } }
        ) {
            override fun getHeaders(): MutableMap<String, String> = headers().toMutableMap()
        }
        queue.add(req)
    }


    private fun sendReply() {
        val thread = _state.value.selectedThread
        val content = _state.value.replyText.trim()
        if (content.isBlank()) return

        _state.update { it.copy(loading = true, error = null) }

        val url = "${_state.value.baseUrl}/api/v1/ForumReply"

        val body = JSONObject().apply {
            put("Id", 0)
            put("ForumThreadId", thread.id)
            put("Content", content)
            put("AuthorUserId", state.value.userId)
            put("AuthorName", state.value.name)
        }

        val req = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            body,
            { _ ->
                _state.update { it.copy(replyText = "") }
                //loadThread(thread.id)
                loadReplies(state.value.selectedThread.id)
            },
            { err ->
                val status = err.networkResponse?.statusCode
                val data = err.networkResponse?.data?.let { String(it) }

                _state.update {
                    it.copy(
                        loading = false,
                        error = "HTTP $status: $data"
                    )
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> = headers().toMutableMap()
        }

        queue.add(req)
    }

    private fun parseThread(o: JSONObject): ForumThread {
        if(o.optInt("repliesCount") != null || o.optInt("repliesCount") != 0){
            loadReplies(o.optInt("id"))
        }


        return ForumThread(
            id = o.optInt("id"),
            title = o.optString("title"),
            content = o.optString("content"),
            category = o.optString("category", null),
            subjectId = o.optInt("subjectId"),
            facultyId = o.optInt("facultyId"),
            authorUserId = o.optString("authorUserId", null),
            authorName = o.optString("authorName", null),
            createdAt = o.optString("createdAt", null),
            repliesCount = o.optInt("repliesCount"),
            replies = state.value.selectedReplies
        )
    }

    private fun loadReplies(id: Int) {
        val url = "${_state.value.baseUrl}/api/v1/ForumReply"
        val req = object : JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { resp ->
                try {
                    val replies = parseReply(resp)
                    _state.update { it.copy(loading = false, selectedReplies = replies.filter { it.forumThreadId == id }) }
                } catch (e: Exception) {
                    _state.update { it.copy(loading = false, error = e.toString()) }
                }
            },
            { err -> _state.update { it.copy(loading = false, error = err.toString()) } }
        ) {
            override fun getHeaders(): MutableMap<String, String> = headers().toMutableMap()
        }
        queue.add(req)
    }

    private fun parseReply(array: JSONArray): List<ForumReply>{
        val replies = mutableListOf<ForumReply>()
        if (array != null) {
            for (i in 0 until array.length()) {
                val r = array.getJSONObject(i)
                replies.add(
                    ForumReply(
                        id = r.optInt("id"),
                        forumThreadId = r.optInt("forumThreadId"),
                        forumThread = r.optString("forumThread", null),
                        authorUserId = r.optString("authorUserId", null),
                        authorName = r.optString("authorName", null),
                        content = r.optString("content"),
                        createdAt = r.optString("createdAt", null)
                    )
                )
            }
        }
        return replies
    }

    private fun headers(): Map<String, String> = hashMapOf(
        "ApiKey" to _state.value.apiKey,
        "Accept" to "application/json"
    )
}


