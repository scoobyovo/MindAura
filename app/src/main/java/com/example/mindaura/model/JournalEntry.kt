package com.example.mindaura.model

import android.app.Activity
import com.google.firebase.firestore.auth.User
import java.util.Date
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date : Date,
    val mood : Mood,
    val activities : List<Activities>,
    val user_id : String,
    val notes : String,
    val reflection : String,
    val gratitude : String
)
