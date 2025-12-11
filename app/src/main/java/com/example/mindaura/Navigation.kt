package com.example.mindaura

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mindaura.ui.theme.MindAuraTheme

/**
 * Bottom navigation bar for the app, displayed only when the user is authenticated.
 *
 * Provides navigation to the Home, Inspire, and Profile screens.
 * Highlights the currently selected destination.
 *
 * @param navController Navigation controller to navigate between screens.
 * @param authViewModel ViewModel managing authentication state.
 */
@Composable
fun BottomNav(navController: NavController, authViewModel: AuthenticationViewModel){
    val authState by authViewModel.authState.observeAsState()

    if(authState is AuthState.Authenticated){
        NavigationBar(containerColor = MindAuraTheme.colorScheme.background) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val ic_home = painterResource(id = R.drawable.home)
            val ic_inspire = painterResource(id = R.drawable.sparkle)
            val ic_profile = painterResource(id = R.drawable.profile)

            NavigationBarItem(
                selected = currentDestination?.route == Destination.Home.route,
                onClick = {
                    navController.navigate(Destination.Home.route){
                        popUpTo(Destination.Home.route)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(painter = ic_home, contentDescription = "home", tint = Color.Unspecified, modifier = Modifier.size(45.dp)) }
            )

            NavigationBarItem(
                selected = currentDestination?.route == Destination.Inspire.route,
                onClick = {
                    navController.navigate(Destination.Inspire.route){
                        popUpTo(Destination.Inspire.route)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(painter = ic_inspire, contentDescription = "inspire", tint = Color.Unspecified, modifier = Modifier.size(45.dp)) }
            )

            NavigationBarItem(
                selected = currentDestination?.route == Destination.Profile.route,
                onClick = {
                    navController.navigate(Destination.Profile.route){
                        popUpTo(Destination.Profile.route)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(painter = ic_profile, contentDescription = "profile", tint = Color.Unspecified, modifier = Modifier.size(45.dp)) }
            )
        }
    }
}
