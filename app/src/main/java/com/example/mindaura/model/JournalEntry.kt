package com.example.mindaura.model

/**
 * Represents a journal entry created by a user.
 *
 * @property id Unique identifier for the journal entry.
 * @property date Timestamp (in milliseconds) representing the day of the entry.
 * @property mood The user's mood for that day.
 * @property activities List of activities the user selected or performed.
 * @property user_id ID of the user who created the entry.
 * @property notes Additional notes entered by the user.
 * @property reflection User's personal reflection for the day.
 * @property gratitude List of items the user is grateful for.
 */
data class JournalEntry(
    var id : String = "",
    val date : Long = 0L,
    val mood : Mood? = null,
    val activities : List<Activities>? = emptyList(),
    val user_id : String? = "",
    val notes : String? = "",
    val reflection : String? = "",
    val gratitude : List<String>? = emptyList()
)
