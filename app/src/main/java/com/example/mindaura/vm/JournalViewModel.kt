package com.example.mindaura.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindaura.model.JournalEntry
import com.example.mindaura.repository.JournalEntryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: JournalEntryRepo) : ViewModel() {

    private val _journalEntries = MutableStateFlow<List<JournalEntry>>(emptyList())
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries

    init {
        // Load entries for the current user when the ViewModel is created
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            repository.getEntries().collect { entries ->
                _journalEntries.value = entries
            }
        }
    }

    fun addJournalEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.insert(entry)
        }
    }
}
