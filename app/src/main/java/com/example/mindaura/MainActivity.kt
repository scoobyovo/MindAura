package com.example.mindaura

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coop2_fbauthentication.screens.HomePage
import com.example.coop2_fbauthentication.screens.LoginPage
import com.example.coop2_fbauthentication.screens.SignupPage
import com.example.mindaura.screens.InspirePage
import com.example.mindaura.screens.JournalEntryPage
import com.example.mindaura.screens.ProfilePage
import com.example.mindaura.ui.theme.MindAuraTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindAuraTheme {
                val authViewModel : AuthenticationViewModel by viewModels()
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MindAuraTheme.colorScheme.background
                )
                {
                    Scaffold(containerColor = MindAuraTheme.colorScheme.background,
                        bottomBar = {
                            /*
                             UPDATE THIS EVENTUALLY> IT ONLY RUNS ONCE
                             */
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
                                    authViewModel = authViewModel)
                            }

                            composable(Destination.Signup.route) {
                                SignupPage(modifier = Modifier,
                                    navController = navController,
                                    authViewModel = authViewModel)
                            }

                            composable(Destination.Inspire.route) {
                                InspirePage()
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

                                JournalEntryPage(date = date)
                            }
                        }
                    }
                }

            }
        }
    }
}