package com.example.mindaura.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class QuoteData(
    @Json(name = "q")
    var quote: String?,
    @Json(name = "a")
    var author: String?
)