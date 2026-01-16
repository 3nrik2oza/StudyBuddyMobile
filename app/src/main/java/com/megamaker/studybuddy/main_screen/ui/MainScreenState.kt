package com.megamaker.studybuddy.main_screen.ui

import com.megamaker.studybuddy.data.Faculty

data class MainScreenState(
    val urlString: String = "https://studdybuddyapp.azurewebsites.net/api/v1/Faculties",
    val faculties: List<Faculty> = emptyList(),
    val error: String = "",

    val showCreateFacultyDialog: Boolean = false,
    val facultyName: String = "",
    val facultySlug: String = "",
)

/*
        val request = object : JsonArrayRequest(
            Method.GET,
            state.value.urlString,
            null,
            { response ->
                try {
                    val faculties = mutableListOf<Faculty>()
                    for (i in 0 until response.length()) {
                        val f = Faculty(
                            response.getJSONObject(i).getString("id"),
                            response.getJSONObject(i).getString("name"),
                            response.getJSONObject(i).getString("slug")
                        )
                        faculties.add(f)
                    }
                    _state.update { it.copy(faculties = faculties, facultyName = "", facultySlug = "") }
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.toString()) }
                }
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
        queue.add(request)*/