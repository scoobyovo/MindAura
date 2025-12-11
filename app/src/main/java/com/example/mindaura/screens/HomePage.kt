package com.example.mindaura.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mindaura.AuthState
import com.example.mindaura.AuthenticationViewModel
import com.example.mindaura.screens.CalendarView
import com.example.mindaura.screens.JournalEntryPage
import com.example.mindaura.vm.JournalViewModel
import com.google.firebase.auth.FirebaseAuth
import java.nio.file.WatchEvent

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
        Text(text = "Home page", fontSize = 32.sp)
        Text(text = "welcome ${user?.email}")

        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Sign out")
        }
        CalendarView(modifier = Modifier, navController, journalVM, user?.uid!!)
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