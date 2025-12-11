package com.example.mindaura.vm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindaura.QuoteUiState
import com.example.mindaura.Storage
import com.example.mindaura.api.Api
import com.example.mindaura.dataStore
import com.example.mindaura.model.QuoteData
import com.example.mindaura.model.Reflection
import com.example.mindaura.repo.QuoteRepo
import com.example.mindaura.repo.ReflectionRepo
import com.example.mindaura.viewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

class QuoteViewModel(context : Context) : ViewModel() {

    private val quoteRepo = QuoteRepo(Api.retrofitService, Storage(context.dataStore))
    private val reflectionRepo = ReflectionRepo(FirebaseFirestore.getInstance())
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState

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

    fun openQuote(quote : QuoteData)
    {
        _uiState.value = _uiState.value.copy(openedQuote = quote)
        _uiState.value = _uiState.value.copy(openedReflection = null)
    }

    fun clearOpenedReflection() {
        _uiState.value = _uiState.value.copy(openedReflection = null)
    }

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

    fun updateReflection(reflection: Reflection, newText: String) {
        viewModelScope.launch {
            val updatedReflection = reflection.copy(
                text = newText,
                date = System.currentTimeMillis()
            )
            reflectionRepo.updateReflection(updatedReflection)
        }
    }

    fun deleteReflection(reflection: Reflection) {
        viewModelScope.launch {
            reflectionRepo.deleteReflection(reflection.id)
        }
    }

    fun openReflection(reflectionId: String) {
        viewModelScope.launch {
            val opened = reflectionRepo.getReflectionById(reflectionId)
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

