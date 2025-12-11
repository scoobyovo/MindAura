package com.example.mindaura

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.Preferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Storage(private val dataStore: DataStore<Preferences>) {
    private val LAST_RANDOM_FETCH_KEY = longPreferencesKey("last_random_quotes_fetch")

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLastRandomFetchDate(): LocalDate? {
        val prefs = dataStore.data.first()
        val epochDay = prefs[LAST_RANDOM_FETCH_KEY]
        return epochDay?.let {LocalDate.ofEpochDay(it)}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun setLastRandomFetchDate(date: LocalDate) {
        dataStore.edit { prefs ->
            prefs[LAST_RANDOM_FETCH_KEY] = date.toEpochDay()
        }
    }
}
