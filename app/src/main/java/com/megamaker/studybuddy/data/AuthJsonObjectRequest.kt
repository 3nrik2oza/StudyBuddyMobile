package com.megamaker.studybuddy.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.runBlocking

class AuthJsonObjectRequest(
    context: Context,
    method: Int,
    url: String,
    body: org.json.JSONObject?,
    onSuccess: (org.json.JSONObject) -> Unit,
    onError: (com.android.volley.VolleyError) -> Unit
) : JsonObjectRequest(method, url, body, onSuccess, onError) {

    private val authStore = AuthStore(context)

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()

        headers["Accept"] = "application/json"
        headers["ApiKey"] = "SecretKey"

        val token = authStore.getTokenBlocking()
        if (!token.isNullOrBlank()) {
            headers["Authorization"] = "Bearer $token"
        }

        return headers
    }
}
