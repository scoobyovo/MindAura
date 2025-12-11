package com.example.mindaura.db.vm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindaura.QuoteUiState
import com.example.mindaura.Storage
import com.example.mindaura.api.Api
import com.example.mindaura.dataStore
import com.example.mindaura.model.QuoteData
import com.example.mindaura.model.Reflection
import com.example.mindaura.db.repo.QuoteRepo
import com.example.mindaura.db.repo.ReflectionRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing quotes and reflections.
 * Handles fetching quotes, opening and clearing quotes, and CRUD operations for reflections.
 *
 * @param context Context used for initializing data storage and repositories.
 */
class QuoteViewModel(context : Context) : ViewModel() {

    private val quoteRepo = QuoteRepo(Api.retrofitService, Storage(context.dataStore))
    private val reflectionRepo = ReflectionRepo(FirebaseFirestore.getInstance())
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    /**
     * Loads quotes and the daily quote for the user.
     *
     * @param userId The ID of the user for whom to load quotes.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadQuotes(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val randomQuotes = quoteRepo.getQuotes()
                val dailyQuote = quoteRepo.getDailyQuote()

                _uiState.value = _uiState.value.copy(
                    quotes = randomQuotes,
                    dailyQuote = dailyQuote,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Opens a quote in the UI and clears any currently opened reflection.
     *
     * @param quote The [QuoteData] to open.
     */
    fun openQuote(quote : QuoteData)
    {
        _uiState.value = _uiState.value.copy(openedQuote = quote)
        _uiState.value = _uiState.value.copy(openedReflection = null)
    }

    /**
     * Saves a new reflection associated with a quote for the given user.
     *
     * @param quote The quote the reflection is based on.
     * @param text The reflection text entered by the user.
     * @param userId The ID of the user saving the reflection.
     */
    fun saveReflection(quote: QuoteData, text: String, userId: String) {
        viewModelScope.launch {
            val reflection = Reflection(
                id = "",
                date = System.currentTimeMillis(),
                quote_text = quote.quote,
                quote_author = quote.author,
                user_id = userId,
                text = text
            )
            reflectionRepo.saveReflection(reflection)
        }
    }

    /**
     * Updates an existing reflection with new text and a current timestamp.
     *
     * @param reflection The existing [Reflection] to update.
     * @param newText The updated reflection text.
     */
    fun updateReflection(reflection: Reflection, newText: String) {
        viewModelScope.launch {
            val updatedReflection = reflection.copy(
                text = newText,
                date = System.currentTimeMillis()
            )
            reflectionRepo.updateReflection(updatedReflection)
        }
    }

    /**
    * Deletes a reflection from Firestore for the current user.
    *
    * @param reflection The [Reflection] to delete.
    */
    fun deleteReflection(reflection: Reflection) {
        viewModelScope.launch {
            reflectionRepo.deleteReflection(userId, reflection.id)
        }
    }

    fun openReflection(reflectionId: String) {
        viewModelScope.launch {
            val opened = reflectionRepo.getReflectionById(userId, reflectionId)
            _uiState.value = _uiState.value.copy(openedReflection = opened)
            _uiState.value = _uiState.value.copy(openedQuote = null)
        }
    }

    fun observeReflections(userId: String) {
        viewModelScope.launch {
            reflectionRepo.getReflectionsFlow(userId).collect { list ->
                _uiState.value = _uiState.value.copy(reflections = list)
            }
        }
    }

}

