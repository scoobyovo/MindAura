package com.example.mindaura.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Reflection (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date : Date,
    val quote : Quote,
    val user_id : String,
    val text : String,
)