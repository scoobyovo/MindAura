package com.example.mindaura.model

import androidx.annotation.DrawableRes
import com.example.mindaura.R

/**
 * Represents the different moods a user can select.
 * Each mood has an associated emoji icon.
 */
// @DrawableRes val icon: Int
enum class Mood() {
    HAPPY,
    CONTENT,
    MEH,
    SAD,
    AWFUL,
}

