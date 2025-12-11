package com.example.mindaura.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mindaura.Storage
import com.example.mindaura.api.QuoteService
import com.example.mindaura.model.QuoteData
import java.time.LocalDate

class QuoteRepo(private val service: QuoteService,
    private val storage: Storage
) {
    private var cachedRandomQuotes: List<QuoteData> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getQuotes(): List<QuoteData> {
        val today = LocalDate.now()
        val lastFetch = storage.getLastRandomFetchDate()
        return if (lastFetch == today && cachedRandomQuotes.isNotEmpty()) {
            Log.i("QUOTE API", "Returning cached random quotes.\n lastfetched = " +
                    "${lastFetch} todays date = ${today}")
            cachedRandomQuotes
        } else {
            try {
                val quotes = service.getRandomQuotes().take(5)
                Log.i("QUOTE API", "API RETURNED ${quotes.size} random quotes")
                cachedRandomQuotes = quotes
                storage.setLastRandomFetchDate(today)
                quotes
            } catch (e: Exception) {
                cachedRandomQuotes
            }
        }
    }

    suspend fun getDailyQuote(): List<QuoteData> {
        return try {
            service.getDailyQuote()?.take(1) ?: emptyList()
        } catch (e: Exception) {
            Log.e("QuoteRepo", "Failed to fetch daily quote")
            emptyList()
        }
    }
}
