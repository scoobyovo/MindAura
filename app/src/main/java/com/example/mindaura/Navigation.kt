package com.example.mindaura

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coop2_fbauthentication.screens.HomePage
import com.example.coop2_fbauthentication.screens.LoginPage
import com.example.coop2_fbauthentication.screens.SignupPage

@Composable
fun Navigation(modifier : Modifier = Modifier, authViewModel : AuthenticationViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){ LoginPage(modifier, navController, authViewModel)  }
        composable("signup"){ SignupPage(modifier, navController, authViewModel)  }
        composable("home"){ HomePage(modifier, navController, authViewModel)  }

    })
}