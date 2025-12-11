package com.example.mindaura.db.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindaura.JournalUiState
import com.example.mindaura.model.Activities
import com.example.mindaura.model.JournalEntry
import com.example.mindaura.model.Mood
import com.example.mindaura.repository.JournalRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel for managing journal entries.
 * Handles loading, saving, updating, and observing journal entries for the current user.
 */
class JournalViewModel : ViewModel() {

    private val repo = JournalRepo(FirebaseFirestore.getInstance())
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState

    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    /**
     * Normalizes a timestamp to the start of the day (00:00:00.000).
     *
     * @param date The timestamp in milliseconds.
     * @return Normalized timestamp at the start of the day.
     */
    private fun normalize(date: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    /**
     * Loads the journal entry for a specific date.
     *
     * @param dateMillis The date to load, in milliseconds.
     */
    fun loadEntryFromDate(dateMillis: Long) {
        val uid = userId ?: return
        val normalizedDay = normalize(dateMillis)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, selectedDate = normalizedDay)

            try {
                repo.getEntryByDate(uid, selectedDate = normalizedDay).collect { entry ->
                    _uiState.value = _uiState.value.copy(currentEntry = entry)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Saves a new journal entry for the current user.
     *
     * @param mood The user's mood.
     * @param activities List of selected activities.
     * @param reflection User's reflection text.
     * @param gratitude List of gratitude items.
     * @param notes Additional notes.
     * @param date Date of the entry in milliseconds.
     * @param onComplete Callback invoked after the entry is saved.
     */
    fun saveEntry(
        mood: Mood?,
        activities: List<Activities>?,
        reflection: String?,
        gratitude: List<String>?,
        notes: String?,
        date: Long,
        onComplete: () -> Unit
    ) {
        val uid = userId ?: return
        val normalizedDate = normalize(date)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val entry = JournalEntry(
                    id = "",
                    date = normalizedDate,
                    mood = mood,
                    activities = activities?.filter { it.isSelected },
                    user_id = uid,
                    notes = notes,
                    reflection = reflection,
                    gratitude = gratitude
                )

                repo.saveEntry(entry)

                loadAllJournaledDays()
                onComplete()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Updates an existing journal entry in the repository.
     *
     * @param entry The [JournalEntry] to update.
     */
    fun updateEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repo.updateEntry(entry.copy())
        }
    }

    /**
     * Loads all dates that have existing journal entries for the current user.
     * Updates the UI state with the list of journaled days.
     */
    fun loadAllJournaledDays() {
        val uid = userId ?: return

        viewModelScope.launch {
            repo.getEntriesFlow(uid).collect { entries ->
                val days = entries.map { normalize(it.date) }.distinct()
                Log.i("SAVED DAYS", "${days}")
                _uiState.value = _uiState.value.copy(journaledDays = days)
            }
        }
    }
}
