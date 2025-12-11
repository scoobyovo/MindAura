package com.example.mindaura.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindaura.AuthenticationViewModel
import com.example.mindaura.JournalUiState
import com.example.mindaura.QuoteUiState
import com.example.mindaura.Storage
import com.example.mindaura.api.Api
import com.example.mindaura.dataStore
import com.example.mindaura.model.Activities
import com.example.mindaura.model.JournalEntry
import com.example.mindaura.model.Mood
import com.example.mindaura.repo.QuoteRepo
import com.example.mindaura.repository.JournalRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class JournalViewModel(
) : ViewModel() {
    private val repo = JournalRepo(FirebaseFirestore.getInstance())
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState

    private val userId : String? = FirebaseAuth.getInstance().currentUser?.uid

    fun loadEntryFromDate(date: Long, userId : String) {
        viewModelScope.launch{
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try{
                _uiState.value = _uiState.value.copy(selectedDate = date)
                repo.getEntryByDate(selectedDate = date, userId = userId)
                    .collect { entryOrNull ->
                        _uiState.value = _uiState.value.copy(currentEntry = entryOrNull)
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun saveEntry(mood : Mood?, activities : List<Activities>?,
                  reflection : String?, gratitude : List<String>?,
                  notes : String?,
                  date: Long,
                  onComplete: ()->  Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val entry = JournalEntry(
                    id = "",
                    date = date,
                    mood = mood,
                    activities = activities,
                    user_id = userId,
                    notes = notes,
                    reflection = reflection,
                    gratitude = gratitude
                )
                repo.saveEntry(entry)
                loadAllJournaledDays(userId = userId)
                onComplete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateEntry(entry: JournalEntry) {
        viewModelScope.launch {
            val updatedEntry = entry.copy(
                notes = entry.notes,
                reflection = entry.reflection,
                gratitude = entry.gratitude,
            )
            repo.updateEntry(updatedEntry)
        }
    }

    fun loadAllJournaledDays(userId: String?) {
        viewModelScope.launch {
            repo.getEntriesFlow(userId).collect { entries ->

                val days = entries.map { entry ->
                    LocalDate.ofEpochDay(entry.date / 86_400_000L)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                }.toSet()

                _uiState.value.copy(journaledDays = days.toList())
            }
        }
    }

}
