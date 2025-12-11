package com.example.mindaura.model

import java.time.LocalDate

/**
 * Represents a user's reflection on a quote.
 *
 * @property id Unique identifier for the reflection.
 * @property date Timestamp (in milliseconds) when the reflection was created or updated.
 * @property quote_text The text of the quote being reflected on.
 * @property quote_author The author of the quote.
 * @property user_id ID of the user who created the reflection.
 * @property text The user's reflection text.
 */
data class Reflection (
    var id: String = "",
    val date : Long = System.currentTimeMillis(),
    val quote_text : String? = "",
    val quote_author : String? = "",
    val user_id : String? = "",
    val text : String? = "",
)