package com.example.mindaura

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindaura.screens.HomePage
import com.example.mindaura.screens.LoginPage
import com.example.mindaura.screens.SignupPage
import com.example.mindaura.ui.theme.MindAuraTheme
/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun Navigation(modifier : Modifier = Modifier, authViewModel : AuthenticationViewModel, journalVM) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){ LoginPage(modifier, navController, authViewModel)  }
        composable("signup"){ SignupPage(modifier, navController, authViewModel)  }
        composable("home"){ HomePage(modifier, navController, authViewModel, journalVM)  }

    })
}
*/

// if user unauthenticaed, make inactive
@Composable
fun BottomNav(navController: NavController, authViewModel: AuthenticationViewModel){
    val authState = authViewModel.authState
    //if(authState.equals(AuthState.Authenticated)){
        NavigationBar(containerColor = MindAuraTheme.colorScheme.background
            ) {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry.value?.destination
            val ic_home = painterResource(id =R.drawable.home)
            val ic_inspire = painterResource(id = R.drawable.sparkle)
            val ic_profile = painterResource(id = R.drawable.profile)

            NavigationBarItem(
                selected = currentDestination?.route == Destination.Home.route,
                onClick = {
                    navController.navigate(Destination.Home.route){
                        popUpTo(Destination.Home.route)
                        launchSingleTop = true
                    } },
                icon = { Icon(painter = ic_home,
                    contentDescription = "home",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(45.dp)
                )}
            )
            NavigationBarItem(
                selected = currentDestination?.route == Destination.Inspire.route,
                onClick = {
                    navController.navigate(Destination.Inspire.route){
                        popUpTo(Destination.Inspire.route)
                        launchSingleTop = true
                    } },
                icon = { Icon(painter = ic_inspire,
                    contentDescription = "Quote inspire page",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(45.dp))}
            )
            NavigationBarItem(
                selected = currentDestination?.route == Destination.Profile.route,
                onClick = {
                    navController.navigate(Destination.Profile.route){
                        popUpTo(Destination.Profile.route)
                        launchSingleTop = true
                    } },
                icon = { Icon(painter = ic_profile,
                    contentDescription = "user profile",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(45.dp))}
            )
        }
    //}
}