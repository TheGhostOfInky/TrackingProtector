package com.theghostofinky.trackingprotector

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object PrefKeys {
    val CONTEXT_COUNT = intPreferencesKey("CONTEXT_COUNT")
    val SHARE_ACTION = intPreferencesKey("SHARE_ACTION")
    val OPEN_ACTION = intPreferencesKey("OPEN_ACTION")
    val INSTANCE = stringPreferencesKey("INSTANCE")
}

class PreferencesDatabase(context: Context) {
    private val dataStore: DataStore<Preferences>

    init {
        dataStore = context.dataStore
    }

    suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data.catch {
            if(it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            (it[key] ?: defaultValue) as T
        }
    }

    suspend fun <T> setPreference(key: Preferences.Key<T>, value: T){
        dataStore.edit {
            it[key] = value
        }
    }
}