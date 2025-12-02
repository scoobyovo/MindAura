package com.example.mindaura

open class Destination(val route : String){
    /*
    Central place for routing destinations
     */
    object Home : Destination("home")
    object Profile : Destination("profile")
    object Inspire : Destination("inspire")
    object JournalEntry : Destination("journalEntry")
    object QuoteReflection : Destination("quoteEntry")
    object Login : Destination("login")
    object Signup : Destination("signup")
}