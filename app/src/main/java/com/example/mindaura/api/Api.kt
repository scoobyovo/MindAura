package com.example.mindaura.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Singleton object providing the Retrofit service for fetching quotes.
 *
 * Uses the ZenQuotes API to fetch random quotes and the daily quote.
 *
 * @property retrofitService Lazy-initialized [QuoteService] for making API calls.
 */
object Api {
    private val BASE_URL = "https://zenquotes.io/api/"
    // quotes returns random quotes, today returns daily

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val retrofitService: QuoteService by lazy{
        retrofit.create(QuoteService::class.java)
    }
}