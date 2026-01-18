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
    private val NAME_KEY = stringPreferencesKey("name_token")
    private val ID_KEY = stringPreferencesKey("id_token")
    private val ID_FACULTY_KEY = stringPreferencesKey("id_faculty")

    val tokenFlow: Flow<String?> = dataStore.data
        .map { it[TOKEN_KEY] }

    val nameFlow: Flow<String?> = dataStore.data
        .map { it[NAME_KEY] }

    val idFlow: Flow<String?> = dataStore.data
        .map { it[ID_KEY] }

    val facultyIdFlow: Flow<String?> = dataStore.data
        .map { it[ID_FACULTY_KEY] }

    suspend fun saveUser(token: String, name: String, userId: String, facultyId: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
        dataStore.edit { it[NAME_KEY] = name }
        dataStore.edit { it[ID_KEY] = userId }
        dataStore.edit { it[ID_FACULTY_KEY] = facultyId }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(TOKEN_KEY) }
        dataStore.edit { it.remove(NAME_KEY) }
        dataStore.edit { it.remove(ID_KEY) }
        dataStore.edit { it.remove(ID_FACULTY_KEY) }
    }

    fun getTokenBlocking(): String? = runBlocking {
        tokenFlow.firstOrNull()
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }

}
