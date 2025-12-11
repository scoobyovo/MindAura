package com.example.mindaura.model

import androidx.annotation.DrawableRes
import com.example.mindaura.R

/**
 * Represents the different moods a user can select.
 * Each mood has an associated emoji icon.
 */
// @DrawableRes val icon: Int
enum class Mood(val label: String = "", @DrawableRes val iconRes: Int = 0) {
    HAPPY("Happy", R.drawable.m_happy),
    CONTENT("Content", R.drawable.m_content),
    FINE("Fine", R.drawable.m_okay),
    SAD("Sad", R.drawable.m_meh),
    AWFUL("Awful", R.drawable.m_awful)
}

