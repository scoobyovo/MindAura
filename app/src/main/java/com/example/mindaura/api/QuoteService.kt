package com.example.mindaura.api

import com.example.mindaura.model.QuoteData
import retrofit2.http.GET

interface QuoteService {
    @GET("quotes")
    suspend fun getRandomQuotes(): List<QuoteData>

    @GET("today")
    suspend fun getDailyQuote(): List<QuoteData>

}
