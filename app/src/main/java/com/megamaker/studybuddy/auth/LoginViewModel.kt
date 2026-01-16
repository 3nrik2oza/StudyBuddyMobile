package com.megamaker.studybuddy.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.megamaker.studybuddy.data.AuthStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject



class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val queue = Volley.newRequestQueue(application)
    private val authStore = AuthStore(application)

    fun onEmailChange(v: String) = _state.update { it.copy(email = v) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v) }

    fun login(onSuccess: () -> Unit) {
        _state.update { it.copy(loading = true, error = null) }

        val body = JSONObject().apply {
            put("email", state.value.email.trim())
            put("password", state.value.password)
        }

        val req = object : JsonObjectRequest(
            Request.Method.POST,
            "https://studdybuddyapp.azurewebsites.net/api/v1/auth/login",
            body,
            { resp ->
                val token = resp.optString("accessToken", "")

                if (token.isBlank()) {
                    _state.update { it.copy(loading = false, error = "Token nije vraćen.") }
                } else {
                    viewModelScope.launch {
                        authStore.saveToken(token)
                        _state.update { it.copy(loading = false) }
                        onSuccess()
                    }
                }
            },
            { err ->
                val status = err.networkResponse?.statusCode
                val msg = when (status) {
                    401 -> "Pogrešan email ili password."
                    404 -> "Login endpoint ne postoji (provjeri deploy i URL)."
                    else -> "Login error (${status ?: "?"})"
                }
                _state.update { it.copy(loading = false, error = msg) }
            }
        ) {
            // Za LOGIN ti ApiKey ne treba (ali može ostati ako želiš)
            override fun getHeaders(): MutableMap<String, String> =
                hashMapOf("Accept" to "application/json")
        }

        queue.add(req)
    }
}
