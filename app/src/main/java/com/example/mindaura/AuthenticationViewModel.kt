package com.example.mindaura

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.text.isEmpty
import kotlin.text.trim

class AuthenticationViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init { // Checking if user was previously logged in on launch
        val currentUser = auth.currentUser
        _authState.value = if (currentUser != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    /*
        Logs a user in if credentials match up in fb
        Authenticates the user if successful
     */
    fun login(email : String, password : String){
        val email = email.trim()
        val password = password.trim()
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Please provide email and password.")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Unable to login.")
                }
            }
    }

    /*
        Signs up a user. Creates a new user in fb
        User is authenticated once signed up
     */
    fun signup(email : String, password : String){
        val email = email.trim()
        val password = password.trim()
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Please provide email and password.")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Unable to login.")
                }
            }
    }

    /*
        Signs out the user
        Unauthenticates
     */
    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

/*
    Handles different auth states
 */
sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}