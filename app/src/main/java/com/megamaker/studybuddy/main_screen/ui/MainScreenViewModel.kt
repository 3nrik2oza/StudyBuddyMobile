// app/src/main/java/com/megamaker/studybuddy/main_screen/MainScreenViewModel.kt
package com.megamaker.studybuddy.main_screen.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.megamaker.studybuddy.data.AuthStore
import com.megamaker.studybuddy.data.Faculty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    private val queue: RequestQueue = Volley.newRequestQueue(application)
    private val authStore = AuthStore(application)

    fun logout() {
        viewModelScope.launch {
            AuthStore(getApplication()).clearToken()
        }
    }

    init {
        val request = object: JsonArrayRequest(
            Method.GET,
            state.value.urlString,
            null,
            { response ->
                try {
                    _state.update { it.copy(error = response.toString()) }
                }catch (e: Exception){
                    _state.update { it.copy(error = e.toString()) }
                }
            },
            { error ->
                _state.update { it.copy(error = error.toString()) }
            }
        ){
            override fun getHeaders(): Map<String, String> = authHeaders()
        }
        queue.add(request)
    }

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.GetData -> Unit
            is MainScreenEvent.ToggleCreateFacultyDialog -> {
                _state.update { it.copy(showCreateFacultyDialog = !it.showCreateFacultyDialog) }
            }
            is MainScreenEvent.OnFacultyNameChange -> {
                _state.update { it.copy(facultyName = event.facultyName) }
            }
            is MainScreenEvent.OnFacultySlugChange -> {
                _state.update { it.copy(facultySlug = event.facultySlug) }
            }
            is MainScreenEvent.CreateNewFaculty -> {
                createNewFaculty()
                _state.update { it.copy(showCreateFacultyDialog = false) }
            }
        }
    }

    private fun authHeaders(): Map<String, String> {
        val token = runBlocking { authStore.tokenFlow.first() }
        val headers = HashMap<String, String>()
        headers["ApiKey"] = "SecretKey"
        if (!token.isNullOrBlank()) headers["Authorization"] = "Bearer $token"
        return headers
    }

    private fun createNewFaculty() {
        val id = state.value.faculties.last().id.toInt() + 1
        val newFaculty = Faculty(id.toString(), state.value.facultyName, state.value.facultySlug)

        val body = JSONObject().apply {
            put("id", id.toString())
            put("name", state.value.facultyName)
            put("slug", state.value.facultySlug)
        }

        val request = object : JsonObjectRequest(
            Method.POST,
            "https://studdybuddyapp.azurewebsites.net/api/v1/Faculties",
            body,
            { _ ->
                _state.update { it.copy(faculties = it.faculties + newFaculty) }
            },
            { error ->
                val status = error.networkResponse?.statusCode
                if (status == 401) {
                    viewModelScope.launch { authStore.clear() }
                }
                _state.update { it.copy(error = error.toString()) }
            }
        ) {
            override fun getHeaders(): Map<String, String> = authHeaders()
        }
        queue.add(request)
    }
}
