package com.example.mindaura.screens

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindaura.Storage
import com.example.mindaura.api.Api
import com.example.mindaura.api.QuoteService
import com.example.mindaura.dataStore
import com.example.mindaura.model.QuoteData
import com.example.mindaura.model.Reflection
import com.example.mindaura.repo.QuoteRepo
import com.example.mindaura.repo.ReflectionRepo
import com.example.mindaura.screens.Cards.QuoteCard
import com.example.mindaura.screens.Cards.ReflectionCard
import com.example.mindaura.toDateString
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.viewModelFactory
import com.example.mindaura.vm.QuoteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InspirePage(quoteVM: QuoteViewModel, navController: NavController){
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    // Observe reflections live
    LaunchedEffect(userId) {
        quoteVM.loadQuotes(userId)
        quoteVM.observeReflections(userId)
    }

    val uiState by quoteVM.uiState.collectAsState()

    // Track current mode
    var showQuotes by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        // Toggle
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { showQuotes = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showQuotes) Color.Blue else Color.LightGray
                )
            ) {
                Text("Quotes")
            }
            Button(
                onClick = { showQuotes = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!showQuotes) Color.Blue else Color.LightGray
                )
            ) {
                Text("My Reflections")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        uiState.error?.let { errorMsg ->
            Text(text = errorMsg)
        }

        LazyColumn {
            if (showQuotes) {
                // Show API quotes
                uiState.dailyQuote.firstOrNull()?.let { daily ->
                    item {
                        QuoteCard(
                            quote = daily,
                            isDaily = true,
                            onClick = {
                                quoteVM.openQuote(daily)
                                navController.navigate("quoteEntry/$userId")
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
                itemsIndexed(uiState.quotes) { index, quote ->
                    QuoteCard(
                        quote = quote,
                        onClick = {
                            quoteVM.openQuote(quote)
                            navController.navigate("quoteEntry/$userId")
                        }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            } else {
                // Show saved reflections
                items(uiState.reflections) { reflection ->
                    ReflectionCard(
                        reflection = reflection,
                        quoteVM,
                        navController,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}