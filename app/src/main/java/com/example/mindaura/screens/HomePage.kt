package com.example.mindaura.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mindaura.AuthState
import com.example.mindaura.AuthenticationViewModel
import com.example.mindaura.ui.theme.MindAuraTheme
import com.example.mindaura.db.vm.JournalViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * Main home page of the app.
 * Displays the app title, welcome message, sign-out button, calendar view, and a button to create a new journal entry.
 *
 * Automatically navigates to the login screen if the user is unauthenticated.
 *
 * @param modifier Modifier for customizing the layout.
 * @param navController Navigation controller used to navigate between screens.
 * @param authViewModel ViewModel handling authentication state and sign-out.
 * @param journalVM ViewModel managing journal entries for the current user.
 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(modifier : Modifier = Modifier,
    navController: NavController,
    authViewModel : AuthenticationViewModel, journalVM : JournalViewModel
)
{
    val authState = authViewModel.authState.observeAsState()
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    Log.d("TEST_FIREBASE", "Current UID: $uid")

    /*
        Launches when authState value has changed
        Listens for auth updates
    */
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "MindAura",
                    style = MindAuraTheme.typography.veryLargeTitle
                )
                user?.email?.let { email ->
                    Text(
                        text = "Welcome, $email",
                        style = MindAuraTheme.typography.labelSmall
                    )
                }
            }

            TextButton(
                onClick = { authViewModel.signout() },
                modifier = Modifier
                    .background(
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .height(40.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Sign out",
                    style = MindAuraTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }

        CalendarView(modifier = Modifier, navController, journalVM)
        TextButton(
            modifier = Modifier.fillMaxSize()
            .padding(end = 5.dp),
            onClick = { navController.navigate("journalEntry") }
        ) {
            Text(
                text = "New entry"
            )
        }
    }
}