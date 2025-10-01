package co.edu.unipiloto.proyectodconvergentes.ui.net

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {
    companion object { private val KEY_JWT = stringPreferencesKey("jwt_token") }

    suspend fun saveToken(token: String) { context.dataStore.edit { it[KEY_JWT] = token } }
    suspend fun getToken(): String? = context.dataStore.data.map { it[KEY_JWT] }.first()
    suspend fun clear() { context.dataStore.edit { it.remove(KEY_JWT) } }
}