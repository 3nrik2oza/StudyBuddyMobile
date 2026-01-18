package com.megamaker.studybuddy.forum

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.megamaker.studybuddy.data.AuthStore
import com.megamaker.studybuddy.data.ForumReply
import com.megamaker.studybuddy.data.ForumThread
import com.megamaker.studybuddy.data.Subject
import com.megamaker.studybuddy.util.getCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ForumViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(ForumState())
    val state = _state.asStateFlow()

    private val queue: RequestQueue = Volley.newRequestQueue(application)

    private val authStore = AuthStore(application)

    init {
        loadThreads()
        viewModelScope.launch {
            val name = authStore.nameFlow.first() ?: ""
            val id = authStore.idFlow.first() ?: ""
            val facultyId = authStore.facultyIdFlow.first() ?: ""
            _state.update { it.copy(name = name, userId = id, newThreadFacultyId = facultyId.toIntOrNull() ?: -1) }
            loadSubjects()
        }
    }

    fun onEvent(event: ForumEvent) {
        when (event) {
            ForumEvent.LoadThreads -> { loadThreads() }
            is ForumEvent.OpenThread -> {}//loadThread(event.threadId)
            ForumEvent.BackToList -> _state.update { it.copy(selectedThread = null, selectedReplies = emptyList(), replyText = "") }

            is ForumEvent.OnReplyTextChange -> _state.update { it.copy(replyText = event.v) }

            ForumEvent.ToggleCreateThreadDialog ->
                _state.update { it.copy(showCreateThreadDialog = !it.showCreateThreadDialog) }

            is ForumEvent.OnNewThreadTitleChange -> _state.update { it.copy(newThreadTitle = event.v) }
            is ForumEvent.OnNewThreadContentChange -> _state.update { it.copy(newThreadContent = event.v) }
            is ForumEvent.OnNewThreadCategoryChange -> _state.update { it.copy(newThreadCategory = event.v) }
            is ForumEvent.OnNewThreadSubjectIdChange -> _state.update { it.copy(newThreadSubjectId = event.subject.id.toIntOrNull() ?: 0) }
            is ForumEvent.OnNewThreadFacultyIdChange -> _state.update { it.copy(newThreadFacultyId = event.v) }

            ForumEvent.CreateThread -> createThread()
        }
    }

    private fun headers(): Map<String, String> = hashMapOf(
        "ApiKey" to _state.value.apiKey,
        "Accept" to "application/json"
    )

    private fun loadThreads() {
        _state.update { it.copy(loading = true, error = null) }

        val url = "${_state.value.baseUrl}/api/v1/ForumThread"
        val req = object : JsonArrayRequest(
            Method.GET,
            url,
            null,
            { resp ->
                try {
                    val list = mutableListOf<ForumThread>()
                    for (i in 0 until resp.length()) {
                        val o = resp.getJSONObject(i)
                        list.add(parseThread(o))
                    }
                    _state.update { it.copy(loading = false, threads = list) }
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

    private fun loadSubjects() {
        _state.update { it.copy(loading = true, error = null) }

        val url = "${_state.value.baseUrl}/api/v1/Subject"
        val req = object : JsonArrayRequest(
            Method.GET,
            url,
            null,
            { resp ->
                try {
                    val list = mutableListOf<Subject>()
                    for (i in 0 until resp.length()) {
                        if(resp.getJSONObject(i).optString("facultyId") == state.value.newThreadFacultyId.toString()){
                            val id = resp.getJSONObject(i).optString("id")
                            val name = resp.getJSONObject(i).optString("name")
                            list.add(Subject(id, name))
                        }

                    }
                    _state.update { it.copy(loading = false, listOfSubjects = list, newThreadSubjectId = list[0].id.toIntOrNull() ?: 0) }
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
    /*
    private fun deleteThread(id: Int) {
        _state.update { it.copy(loading = true, error = null) }

        val url = "${_state.value.baseUrl}/api/v1/ForumThread/$id"
        val req = object : JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            { _ ->
                _state.update { it.copy(selectedThread = null, selectedReplies = emptyList(), replyText = "") }
                loadThreads()
            },
            { err -> _state.update { it.copy(loading = false, error = err.toString()) } }
        ) {
            override fun getHeaders(): MutableMap<String, String> = headers().toMutableMap()
        }
        queue.add(req)
    }*/

    private fun createThread() {
        val title = _state.value.newThreadTitle.trim()
        val content = _state.value.newThreadContent.trim()
        if (title.isBlank() || content.isBlank()) return

        _state.update { it.copy(loading = true, error = null) }

        val url = "${_state.value.baseUrl}/api/v1/ForumThread"
        val body = JSONObject().apply {
            put("Id", state.value.threads.last().id + 1)
            put("Title", title)
            put("Content", content)
            put("Category", _state.value.newThreadCategory)
            put("SubjectId", _state.value.newThreadSubjectId)
            put("FacultyId", _state.value.newThreadFacultyId)
            put("AuthorUserId", _state.value.userId)
            put("AuthorName", _state.value.name)
            //put("createdAt", "")
            put("RepliesCount", 0)
            put("replies", org.json.JSONArray())
        }



        val req = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            body,
            { _ ->
                _state.update {
                    it.copy(
                        showCreateThreadDialog = false,
                        newThreadTitle = "",
                        newThreadContent = "",
                        newThreadCategory = ""
                    )
                }
                loadThreads()
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
        val replies = mutableListOf<ForumReply>()
        val repliesArr = o.optJSONArray("replies")
        if (repliesArr != null) {
            for (i in 0 until repliesArr.length()) {
                val r = repliesArr.getJSONObject(i)
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
            replies = replies
        )
    }
}

