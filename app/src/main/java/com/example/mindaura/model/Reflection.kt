package com.example.mindaura.model

import java.time.LocalDate

data class Reflection (
    var id: String = "",
    val date : Long = System.currentTimeMillis(),
    val quote_text : String? = "",
    val quote_author : String? = "",
    val user_id : String? = "",
    val text : String? = "",
)