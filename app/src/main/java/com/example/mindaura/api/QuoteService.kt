package com.example.mindaura.api

import com.example.mindaura.model.QuoteData
import retrofit2.http.GET



/**
 * Retrofit service interface for fetching quotes from the ZenQuotes API.
 */interface QuoteService {
    /*Fetches a list of random quotes*/
     @GET("quotes")
    suspend fun getRandomQuotes(): List<QuoteData>

    /*Fetches the quote of the day*/
    @GET("today")
    suspend fun getDailyQuote(): List<QuoteData>

}
