package com.megamaker.studybuddy.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "auth")

class AuthStore(context: Context) {

    private val dataStore = context.dataStore
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    val tokenFlow: Flow<String?> = dataStore.data
        .map { it[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }

    fun getTokenBlocking(): String? = runBlocking {
        tokenFlow.firstOrNull()
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }

}
