package com.example.mindaura.model

data class JournalEntry(
    var id : String = "",
    val date : Long = System.currentTimeMillis(),
    val mood : Mood? = null,
    val activities : List<Activities>? = emptyList(),
    val user_id : String? = "",
    val notes : String? = "",
    val reflection : String? = "",
    val gratitude : List<String>? = emptyList()
)
