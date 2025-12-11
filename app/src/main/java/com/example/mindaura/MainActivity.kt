package com.example.mindaura

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindaura.api.Api
import com.example.mindaura.api.QuoteService
import com.example.mindaura.model.QuoteData
import com.example.mindaura.model.Reflection
import com.example.mindaura.repo.QuoteRepo
import com.example.mindaura.screens.HomePage
import com.example.mindaura.screens.InspirePage
import com.example.mindaura.screens.JournalEntryPage
import com.example.mindaura.screens.LoginPage
import com.example.mindaura.screens.ProfilePage
import com.example.mindaura.screens.ReflectionEntryPage
import com.example.mindaura.screens.SignupPage
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.vm.JournalViewModel
import com.example.mindaura.vm.QuoteViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import kotlin.getValue

inline fun <VM : ViewModel> viewModelFactory(crossinline f: ()-> VM) =
    object: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>):T = f() as T
    }
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindAuraTheme {
                val authViewModel : AuthenticationViewModel by viewModels()
                val navController = rememberNavController()
                val context = LocalContext.current
                val journalVM : JournalViewModel = viewModel(
                    factory = viewModelFactory {
                        JournalViewModel()
                    }
                )
                val quoteService = Api.retrofitService
                val storage = Storage(this.dataStore)
                val quoteRepo = QuoteRepo(quoteService, storage)

                val quoteVM: QuoteViewModel = viewModel(
                    factory = viewModelFactory {
                        QuoteViewModel(context)
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MindAuraTheme.colorScheme.background
                )
                {
                    Scaffold(containerColor = MindAuraTheme.colorScheme.background,
                        bottomBar = {
                            if(authViewModel.authState.value == AuthState.Authenticated){
                                BottomNav(navController = navController, authViewModel)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Destination.Login.route,
                            modifier = Modifier.padding(innerPadding)
                        ){
                            composable(Destination.Login.route){
                                LoginPage(modifier = Modifier,
                                    navController = navController,
                                    authViewModel = authViewModel
                                )
                            }

                            composable(Destination.Home.route) {
                                HomePage(modifier = Modifier,
                                    navController = navController,
                                    authViewModel = authViewModel,
                                    journalVM,
                                )
                            }

                            composable(Destination.Signup.route) {
                                SignupPage(modifier = Modifier,
                                    navController = navController,
                                    authViewModel = authViewModel)
                            }

                            composable(Destination.Inspire.route) {
                                InspirePage(quoteVM, navController)
                            }

                            composable(Destination.Profile.route){
                                ProfilePage()
                            }

                            composable(
                                route = "journalEntry/{timestamp}",
                                arguments = listOf(
                                    navArgument("timestamp") { type = NavType.LongType }
                                )
                            ) { backStackEntry ->

                                val timestamp = backStackEntry.arguments?.getLong("timestamp")
                                val date = timestamp?.let { Date(it) }

                                if (date != null) {
                                    JournalEntryPage(
                                        modifier = Modifier,
                                        date = date,
                                        navController = navController,
                                        journalVM = journalVM
                                    )
                                } else {
                                    // should never happen
                                    Text("Invalid date")
                                }
                            }

                            composable(
                                route = "quoteEntry/{userId}?reflectionId={reflectionId}",
                                arguments = listOf(
                                    navArgument("userId") { type = NavType.StringType },
                                    navArgument("reflectionId"){
                                        type = NavType.StringType
                                        defaultValue = ""
                                        nullable = true
                                    }
                                )
                            ) { backStackEntry ->

                                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                                val reflectionId = backStackEntry.arguments?.getString("reflectionId")

                                val uiState by quoteVM.uiState.collectAsState()
                                val quote : QuoteData? = uiState.openedQuote
                                val reflection :Reflection? = uiState.openedReflection

                                ReflectionEntryPage(
                                    quote = quote,
                                    userId = userId,
                                    navController = navController,
                                    quoteVM = quoteVM,
                                    existingReflection = reflection
                                )

                            }

                        }
                    }
                }

            }
        }
    }
}