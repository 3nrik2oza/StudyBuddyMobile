package com.megamaker.studybuddy.main_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.megamaker.studybuddy.data.Faculty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

class MainScreenViewModel(    application: Application): AndroidViewModel(application) {
    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()


    val queue: RequestQueue = Volley.newRequestQueue(application)


    init{
        val request = object : JsonArrayRequest(
            Request.Method.GET,
            state.value.urlString,
            null,
            { response ->
                try {
                    val faculties = mutableListOf<Faculty>()
                    for (i in 0 until response.length()) {
                        val f: Faculty = Faculty(response.getJSONObject(i).getString("id"), response.getJSONObject(i).getString("name"), response.getJSONObject(i).getString("slug"))
                        faculties.add(f)
                    }
                    _state.update { it.copy(faculties = faculties, facultyName = "", facultySlug = "") }
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.toString()) }
                }
            },
            { error ->
                _state.update { it.copy(error = error.toString()) }
            }
        ){
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["ApiKey"] = "SecretKey"
                return headers
            }
        }
        queue.add(request)
    }

    fun onEvent(event: MainScreenEvent){
        when(event){
            is MainScreenEvent.GetData -> {

            }
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

    private fun createNewFaculty(){
        val id =  state.value.faculties.last().id.toInt() +1
        val newFaculty = Faculty(id.toString(), state.value.facultyName, state.value.facultySlug)

        val body = JSONObject().apply {
            put("id", id.toString())
            put("name", state.value.facultyName)
            put("slug", state.value.facultySlug)
        }

        val request = object : JsonObjectRequest(
            Request.Method.POST,
            "https://studdybuddyapp.azurewebsites.net/api/v1/Faculties",
            body,
            { response ->
                _state.update { it.copy(faculties = it.faculties + newFaculty) }
            },
            { error ->
                //_state.update { it.copy(stringList = listOf("test", error.toString())) }
                _state.update { it.copy(error = error.toString()) }
            }
        ){
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["ApiKey"] = "SecretKey"
                return headers
            }
        }
        queue.add(request)
    }

}