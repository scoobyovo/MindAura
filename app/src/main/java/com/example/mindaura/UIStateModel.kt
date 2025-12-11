package com.example.mindaura

import com.example.mindaura.model.Activities
import com.example.mindaura.model.JournalEntry
import com.example.mindaura.model.Mood
import com.example.mindaura.model.QuoteData
import com.example.mindaura.model.Reflection

data class QuoteUiState(
    val quotes: List<QuoteData> = emptyList(),
    val dailyQuote: List<QuoteData> = emptyList(),
    val reflections: List<Reflection> = emptyList(),
    val openedReflection: Reflection? = null,
    val openedQuote: QuoteData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class JournalUiState(
    val selectedDate: Long? = null,
    val journaledDays: List<Long>? = emptyList(),
    val entries: List<JournalEntry> = emptyList(),
    val currentEntry: JournalEntry? = null,

    val selectedMood: Mood? = null,
    val selectedActivities: List<Activities> = emptyList(),

    val notes: String = "",
    val reflection: List<String> = emptyList(),
    val gratitude: String = "",

    val isLoading: Boolean = false,
    val error: String? = null
)

